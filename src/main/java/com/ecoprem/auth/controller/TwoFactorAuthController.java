package com.ecoprem.auth.controller;

import com.ecoprem.auth.dto.TwoFactorSetupResponse;
import com.ecoprem.auth.dto.TwoFactorVerifyRequest;
import com.ecoprem.auth.entity.User;
import com.ecoprem.auth.service.TwoFactorAuthService;
import com.ecoprem.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("/api/auth/2fa")
@RequiredArgsConstructor
public class TwoFactorAuthController {

    private final TwoFactorAuthService twoFactorAuthService;
    private final UserRepository userRepository;

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
}
