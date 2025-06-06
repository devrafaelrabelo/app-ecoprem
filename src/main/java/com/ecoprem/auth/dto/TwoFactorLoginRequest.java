package com.ecoprem.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Dados enviados para verificação do código 2FA após login.")
public class TwoFactorLoginRequest {

    @NotBlank
    @Schema(description = "Código de autenticação de dois fatores", example = "523867")
    private String twoFactorCode;

    @NotBlank
    @Schema(description = "Token temporário recebido após o login inicial", example = "f0a9e3fc-bcc4-4d9c-a1b2-819e4e9a9c61")
    private String tempToken;

    @Schema(description = "Indica se o login deve lembrar o usuário", defaultValue = "false")
    private boolean rememberMe = false;
}
