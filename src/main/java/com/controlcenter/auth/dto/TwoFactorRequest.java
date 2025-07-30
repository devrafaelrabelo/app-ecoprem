package com.controlcenter.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Requisição para verificação de código 2FA via e-mail ou aplicativo autenticador.")
public class TwoFactorRequest {

    @NotBlank
    @Email
    @Schema(description = "E-mail do usuário", example = "rafael@empresa.com")
    private String email;

    @NotBlank
    @Schema(description = "Código de verificação 2FA", example = "123456")
    private String code;
}
