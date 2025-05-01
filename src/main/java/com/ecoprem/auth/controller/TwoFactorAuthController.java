package com.ecoprem.auth.controller;

import com.ecoprem.auth.dto.LoginResponse;
import com.ecoprem.auth.dto.TwoFactorLoginRequest;
import com.ecoprem.auth.dto.TwoFactorSetupResponse;
import com.ecoprem.auth.dto.TwoFactorVerifyRequest;
import com.ecoprem.auth.entity.BackupCode;
import com.ecoprem.auth.entity.Pending2FALogin;
import com.ecoprem.auth.entity.User;
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
import java.util.Optional;

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
    @PostMapping("/disable")
    public ResponseEntity<?> disable2FA(@AuthenticationPrincipal User user) {
        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecret(null);
        userRepository.save(user);
        return ResponseEntity.ok("2FA disabled successfully");
    }

    @PostMapping("/validate-login")
    public ResponseEntity<?> validateLogin2FA(@RequestBody TwoFactorLoginRequest request) {
        Optional<Pending2FALogin> pendingOpt = pending2FALoginRepository.findByTempToken(request.getTempToken());
        if (pendingOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }

        Pending2FALogin pending = pendingOpt.get();

        // Check expiration
        if (pending.getExpiresAt().isBefore(LocalDateTime.now())) {
            pending2FALoginRepository.delete(pending);
            return ResponseEntity.badRequest().body("Expired token");
        }

        User user = pending.getUser();
        boolean validCode = twoFactorAuthService.verifyCode(user.getTwoFactorSecret(), request.getTwoFactorCode());

        // Se falhar no TOTP, tenta backup code
        if (!validCode) {
            boolean validBackup = backupCodeService.validateBackupCode(user, request.getTwoFactorCode());
            if (!validBackup) {
                return ResponseEntity.badRequest().body("Invalid 2FA code");
            }
        }

        // Gerar token final
        String token = jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getName()
        );

        // Limpar o tempToken (opcional)
        pending2FALoginRepository.delete(pending);

        return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), user.getFullName(), true, null));
    }

    @PostMapping("/backup-codes/generate")
    public ResponseEntity<?> generateBackupCodes(@AuthenticationPrincipal User user) {
        List<String> codes = backupCodeService.generateBackupCodes(user, 10);
        return ResponseEntity.ok(codes);
    }

    @GetMapping("/backup-codes")
    public ResponseEntity<?> listBackupCodes(@AuthenticationPrincipal User user) {
        List<BackupCode> codes = backupCodeService.getBackupCodes(user);
        return ResponseEntity.ok(codes);
    }

}
