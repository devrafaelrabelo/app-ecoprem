package com.ecoprem.auth.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;

@Service
public class TwoFactorAuthService {

    // Gera secret (160 bits)
    public String generateSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];  // 160 bits
        random.nextBytes(bytes);
        return Base64.getEncoder().withoutPadding().encodeToString(bytes);
    }

    // Gera URL do Google Authenticator (para QR code)
    public String getOtpAuthURL(String appName, String userEmail, String secret) {
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                appName,
                userEmail,
                secret,
                appName
        );
    }

    // Gera QR code como array de bytes (PNG)
    public byte[] generateQRCodeImage(String barcodeText, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    // Valida código TOTP (6 dígitos)
    public boolean verifyCode(String secret, String code) {
        try {
            long timestep = Instant.now().getEpochSecond() / 30;
            byte[] keyBytes = Base64.getDecoder().decode(secret);
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
            int binary =
                    ((hash[offset] & 0x7F) << 24) |
                            ((hash[offset + 1] & 0xFF) << 16) |
                            ((hash[offset + 2] & 0xFF) << 8) |
                            (hash[offset + 3] & 0xFF);

            int otp = binary % 1_000_000;
            String generatedCode = String.format("%06d", otp);

            return generatedCode.equals(code);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
