package com.controlcenter.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserListDTO {
    private UUID id;
    private String username;
    private String fullName;
    private String email;
    private String cpf;

    private List<String> roles;        // todos os papéis
    private List<String> departments;  // todos os departamentos
    private String position;

    private String status;
    private boolean locked;
    private boolean twoFactorEnabled;
    private boolean emailVerified;
    private boolean passwordCompromised;

    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}