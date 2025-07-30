package com.controlcenter.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Resposta de configuração do 2FA, incluindo chave secreta e QR Code em base64.")
public class TwoFactorSetupResponse {

    @Schema(description = "Chave secreta do 2FA (TOTP)", example = "JBSWY3DPEHPK3PXP")
    private String secret;

    @Schema(description = "Imagem do QR Code codificada em base64", example = "data:image/png;base64,iVBORw0KGgoAAAANS...")
    private String qrCodeImageBase64;
}
