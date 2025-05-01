package com.ecoprem.auth.dto;

import lombok.Data;

@Data
public class TwoFactorSetupResponse {
    private String secret;
    private String qrCodeImageBase64;  // Retorna a imagem em base64 para fácil exibição
}
