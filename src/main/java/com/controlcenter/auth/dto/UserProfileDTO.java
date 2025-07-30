package com.controlcenter.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Perfil do usuário autenticado")
public class UserProfileDTO {

    @Schema(description = "ID do usuário", example = "e6e28546-5601-4840-9804-f61ea2e55c4a")
    private String userId;

    @Schema(description = "Nome de usuário", example = "admin")
    private String username;

    @Schema(description = "Email do usuário", example = "admin@example.com")
    private String email;

    @Schema(description = "Nome completo", example = "Admin Principal")
    private String fullName;

    @Schema(description = "Se o 2FA está habilitado")
    private boolean twoFactorEnabled;
}