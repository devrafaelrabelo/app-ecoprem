package com.controlcenter.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informações básicas da sessão do usuário autenticado.")
public class SessionUserResponse {

    @Schema(description = "Indica se a sessão atual é válida", example = "true")
    private boolean valid;

    @Schema(description = "Username do usuário autenticado", example = "rafael")
    private String username;

    @Schema(description = "Nome completo do usuário", example = "Rafael Rabelo Gonçalves")
    private String fullName;

    @Schema(description = "Lista de permissões efetivas do usuário", example = "[\"CREATE_USER\", \"DELETE_PHONE\"]")
    private List<String> permissions;

    @Schema(description = "Indica se o segundo fator de autenticação está habilitado", example = "true")
    private boolean twoFactorEnabled;
}