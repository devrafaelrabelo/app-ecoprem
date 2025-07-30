package com.controlcenter.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Dados enviados para verificação do código 2FA após login.")
public class TwoFactorLoginRequest {

    @NotBlank
    @Schema(description = "Código de autenticação de dois fatores", example = "523867")
    private String twoFactorCode;

    @Schema(description = "Indica se o login deve lembrar o usuário", defaultValue = "false")
    private boolean rememberMe = false;
}
