package com.controlcenter.auth.service;

import com.controlcenter.auth.cache.AuthCacheRegistry;
import com.controlcenter.auth.dto.BackupCodeResponse;
import com.controlcenter.auth.dto.LoginWithRefreshResponse;
import com.controlcenter.auth.dto.TwoFactorLoginRequest;
import com.controlcenter.auth.dto.TwoFactorSetupResponse;
import com.controlcenter.auth.repository.Pending2FALoginRepository;
import com.controlcenter.exceptions.exception.Expired2FATokenException;
import com.controlcenter.exceptions.exception.Invalid2FACodeException;
import com.controlcenter.exceptions.exception.Invalid2FATokenException;
import com.controlcenter.user.repository.UserRepository;
import com.controlcenter.auth.util.JwtCookieUtil;
import com.controlcenter.entity.auth.Pending2FALogin;
import com.controlcenter.entity.user.User;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwoFactorAuthService {

    private static final int MAX_2FA_ATTEMPTS = 2;

    private final AuthCacheRegistry cacheRegistry;
    private final UserRepository userRepository;
    private final Pending2FALoginRepository pending2FALoginRepository;
    private final BackupCodeService backupCodeService;
    private final AuthService authService;
    private final JwtCookieUtil jwtCookieUtil;
    private final LoginFinalizerService loginFinalizerService;

    public String generateSecret() {
        byte[] buffer = new byte[20];
        new SecureRandom().nextBytes(buffer);
        return new Base32().encodeToString(buffer).replace("=", "");
    }

    public String getOtpAuthURL(String appName, String userEmail, String secret) {
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", appName, userEmail, secret, appName);
    }

    public byte[] generateQRCodeImage(String barcodeText, int width, int height) throws Exception {
        BitMatrix bitMatrix = new QRCodeWriter().encode(barcodeText, BarcodeFormat.QR_CODE, width, height);
        try (ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        }
    }

    public boolean verifyCode(String secret, String code) {
        try {
            code = code.trim().replace(" ", "");
            long timestep = Instant.now().getEpochSecond() / 30;
            byte[] keyBytes = new Base32().decode(secret);
            SecretKeySpec signKey = new SecretKeySpec(keyBytes, "HmacSHA1");

            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signKey);

            byte[] data = new byte[8];
            for (int i = 7; i >= 0; i--) {
                data[i] = (byte) (timestep & 0xFF);
                timestep >>= 8;
            }

            byte[] hash = mac.doFinal(data);
            int offset = hash[hash.length - 1] & 0xF;

            int binary = ((hash[offset] & 0x7F) << 24)
                    | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8)
                    | (hash[offset + 3] & 0xFF);

            String generatedCode = String.format("%06d", binary % 1_000_000);
            return generatedCode.equals(code);

        } catch (Exception e) {
            log.warn("Erro ao validar código 2FA: {}", e.getMessage(), e);
            return false;
        }
    }

    public TwoFactorSetupResponse setupTwoFactorForUser(User user) {
        String secret = generateSecret();
        String otpAuthUrl = getOtpAuthURL("ControleCenter", user.getEmail(), secret);

        byte[] qrImage;
        try {
            qrImage = generateQRCodeImage(otpAuthUrl, 300, 300);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar QR Code 2FA", e);
        }

        user.setTwoFactorSecret(secret);
        userRepository.save(user);

        return TwoFactorSetupResponse.builder()
                .secret(secret)
                .qrCodeImageBase64(Base64.getEncoder().encodeToString(qrImage))
                .build();
    }

    public boolean verifyAndEnableTwoFactor(User user, String code) {
        boolean isValid = verifyCode(user.getTwoFactorSecret(), code);
        if (isValid) {
            user.setTwoFactorEnabled(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public void disableTwoFactor(User user) {
        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecret(null);
        userRepository.save(user);
        backupCodeService.deleteAllBackupCodes(user);
    }

    public LoginWithRefreshResponse validate2FALogin(TwoFactorLoginRequest request,
                                                     HttpServletRequest httpRequest,
                                                     HttpServletResponse response) {
        String tempToken = jwtCookieUtil.extractTempTokenFromCookie(httpRequest)
                .orElseThrow(() -> new Invalid2FATokenException("Token 2FA não encontrado no cookie."));

        Pending2FALogin pending = pending2FALoginRepository.findByTempToken(tempToken)
                .orElseThrow(() -> new Invalid2FATokenException("Token 2FA inválido ou expirado."));

        try {
            if (pending.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new Expired2FATokenException("O token 2FA expirou. Faça login novamente.");
            }

            User user = pending.getUser();
            Cache<UUID, Integer> attemptsCache = cacheRegistry.getTwoFactorAttemptsPerUser();

            int attempts = attemptsCache.get(user.getId(), id -> 0);
            if (attempts >= MAX_2FA_ATTEMPTS) {
                throw new Invalid2FACodeException("Muitas tentativas incorretas. Tente novamente mais tarde.");
            }

            boolean validTotp = verifyCode(user.getTwoFactorSecret(), request.getTwoFactorCode());
            boolean validBackup = backupCodeService.validateBackupCode(user, request.getTwoFactorCode());

            if (!validTotp && !validBackup) {
                attemptsCache.put(user.getId(), attempts + 1);
                throw new Invalid2FACodeException("O código 2FA está incorreto.");
            }

            // Sucesso: limpa tentativas
            attemptsCache.invalidate(user.getId());

            return loginFinalizerService.finalizeLogin(user, request.isRememberMe(), httpRequest, response);

        } finally {
            pending2FALoginRepository.delete(pending);
            jwtCookieUtil.clearTempTokenCookie(response);
        }
    }

    public List<BackupCodeResponse> listBackupCodes(User user) {
        return backupCodeService.getBackupCodes(user).stream()
                .map(code -> new BackupCodeResponse(
                        code.getId(),
                        code.getCode(),
                        code.isUsed(),
                        code.getCreatedAt(),
                        code.getUsedAt()
                ))
                .toList();
    }

    public List<String> generateBackupCodes(User user, int quantity) {
        return backupCodeService.generateBackupCodes(user, quantity);
    }

    public void deleteAllBackupCodes(User user) {
        backupCodeService.deleteAllBackupCodes(user);
    }
}
