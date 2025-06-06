package com.ecoprem.auth.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.time.Instant;

/**
 * Serviço de suporte ao fluxo de autenticação de dois fatores (2FA) usando algoritmo TOTP compatível com Google Authenticator.
 */
@Slf4j
@Service
public class TwoFactorAuthService {

    /**
     * Gera um segredo base32 (160 bits) para configurar o 2FA do usuário.
     */
    public String generateSecret() {
        byte[] buffer = new byte[20];  // 160 bits
        new SecureRandom().nextBytes(buffer);
        return new Base32().encodeToString(buffer).replace("=", "");
    }

    /**
     * Cria a URL do padrão otpauth:// usada para gerar o QR Code que será escaneado pelo aplicativo autenticador.
     */
    public String getOtpAuthURL(String appName, String userEmail, String secret) {
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                appName, userEmail, secret, appName
        );
    }

    /**
     * Gera imagem PNG de QR Code a partir da URL otpauth, retornando como array de bytes.
     */
    public byte[] generateQRCodeImage(String barcodeText, int width, int height) throws Exception {
        BitMatrix bitMatrix = new QRCodeWriter().encode(barcodeText, BarcodeFormat.QR_CODE, width, height);
        try (ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        }
    }

    /**
     * Valida se o código TOTP enviado pelo usuário é válido com base no segredo atual.
     */
    public boolean verifyCode(String secret, String code) {
        try {
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
}
