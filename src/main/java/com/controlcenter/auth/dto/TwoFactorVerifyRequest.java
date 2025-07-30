package com.controlcenter.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Código TOTP enviado para ativar o 2FA")
public class TwoFactorVerifyRequest {

    @NotBlank
    @Schema(description = "Código de verificação gerado pelo app autenticador", example = "123456")
    private String code;
}
