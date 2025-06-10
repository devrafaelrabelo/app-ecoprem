package com.ecoprem.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserProfileDTO {
    private String userId;
    private String username;
    private String email;
    private String fullName;
    @Schema(description = "Lista de papéis (roles) do usuário", example = "[\"ADMIN\", \"USER\"]")
    private List<String> roles;
    private boolean twoFactorEnabled;
}