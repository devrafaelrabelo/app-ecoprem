package com.ecoprem.auth.controller;

import com.ecoprem.auth.dto.*;
import com.ecoprem.auth.entity.BackupCode;
import com.ecoprem.auth.entity.Pending2FALogin;
import com.ecoprem.auth.entity.User;
import com.ecoprem.auth.exception.Expired2FATokenException;
import com.ecoprem.auth.exception.Invalid2FACodeException;
import com.ecoprem.auth.exception.Invalid2FATokenException;
import com.ecoprem.auth.repository.Pending2FALoginRepository;
import com.ecoprem.auth.security.JwtTokenProvider;
import com.ecoprem.auth.service.BackupCodeService;
import com.ecoprem.auth.service.TwoFactorAuthService;
import com.ecoprem.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/auth/2fa")
@RequiredArgsConstructor
public class TwoFactorAuthController {

    private final TwoFactorAuthService twoFactorAuthService;
    private final UserRepository userRepository;
    private final Pending2FALoginRepository pending2FALoginRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BackupCodeService backupCodeService;

    // Setup: Gera secret + QR code
    @PostMapping("/setup")
    public ResponseEntity<TwoFactorSetupResponse> setup2FA(@AuthenticationPrincipal User user) throws Exception {
        String secret = twoFactorAuthService.generateSecret();
        String otpAuthUrl = twoFactorAuthService.getOtpAuthURL("EcoPrem", user.getEmail(), secret);

        byte[] qrImage = twoFactorAuthService.generateQRCodeImage(otpAuthUrl, 300, 300);
        String qrCodeImageBase64 = Base64.getEncoder().encodeToString(qrImage);

        // Salva temporariamente (ou só ao confirmar)
        user.setTwoFactorSecret(secret);
        userRepository.save(user);

        TwoFactorSetupResponse response = new TwoFactorSetupResponse();
        response.setSecret(secret);
        response.setQrCodeImageBase64(qrCodeImageBase64);

        return ResponseEntity.ok(response);
    }

    // Verifica o código para ativar 2FA
    @PostMapping("/verify")
    public ResponseEntity<?> verify2FA(@AuthenticationPrincipal User user,
                                       @RequestBody TwoFactorVerifyRequest request) {
        boolean isValid = twoFactorAuthService.verifyCode(user.getTwoFactorSecret(), request.getCode());

        if (isValid) {
            user.setTwoFactorEnabled(true);
            userRepository.save(user);
            return ResponseEntity.ok("2FA enabled successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid code");
        }
    }

    // Desativa 2FA
    @PostMapping("/2fa/disable")
    public ResponseEntity<?> disableTwoFactor(@AuthenticationPrincipal User user) {
        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecret(null);
        userRepository.save(user);

        // Limpa os backup codes
        backupCodeService.deleteAllBackupCodes(user);

        return ResponseEntity.ok("2FA disabled and backup codes removed.");
    }

    @PostMapping("/validate-login")
    public ResponseEntity<?> validateLogin2FA(@RequestBody TwoFactorLoginRequest request) {
        Pending2FALogin pending = pending2FALoginRepository.findByTempToken(request.getTempToken())
                .orElseThrow(() -> new Invalid2FATokenException("Invalid or expired 2FA token."));

        // ✅ Checa expiração
        if (pending.getExpiresAt().isBefore(LocalDateTime.now())) {
            pending2FALoginRepository.delete(pending);  // cleanup
            throw new Expired2FATokenException("The 2FA token has expired. Please login again.");
        }

        User user = pending.getUser();

        // ✅ Tenta validar código TOTP
        boolean validCode = twoFactorAuthService.verifyCode(user.getTwoFactorSecret(), request.getTwoFactorCode());

        // Se falhar no TOTP, tenta backup code
        if (!validCode) {
            boolean validBackup = backupCodeService.validateBackupCode(user, request.getTwoFactorCode());
            if (!validBackup) {
                throw new Invalid2FACodeException("The 2FA code is incorrect.");
            }
        }

        // 🔥 Gerar token final
        String token = jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getName()
        );

        // Limpa o tempToken (opcional)
        pending2FALoginRepository.delete(pending);

        return ResponseEntity.ok(
                new LoginResponse(
                        token,
                        user.getUsername(),
                        user.getFullName(),
                        true,
                        null
                )
        );
    }

    @GetMapping("/backup-codes")
    public ResponseEntity<?> listBackupCodes(@AuthenticationPrincipal User user) {
        List<BackupCode> codes = backupCodeService.getBackupCodes(user);

        List<BackupCodeResponse> response = codes.stream()
                .map(code -> new BackupCodeResponse(
                        code.getId(),
                        code.getCode(),
                        code.isUsed(),
                        code.getCreatedAt(),
                        code.getUsedAt()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/backup-codes/generate")
    public ResponseEntity<?> generateBackupCodes(@AuthenticationPrincipal User user) {
        List<String> codes = backupCodeService.generateBackupCodes(user, 10);
        return ResponseEntity.ok(codes);
    }

    @PostMapping("/backup-codes/regenerate")
    public ResponseEntity<?> regenerateBackupCodes(@AuthenticationPrincipal User user) {
        if (!user.isTwoFactorEnabled()) {
            return ResponseEntity.badRequest().body("2FA is not enabled for this account.");
        }

        List<String> codes = backupCodeService.regenerateBackupCodes(user, 10);
        return ResponseEntity.ok(codes);
    }

}
