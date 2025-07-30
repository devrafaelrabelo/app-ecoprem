package com.controlcenter.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AdminUserResponseDTO {
    private UUID id;
    private String username;
    private String email;
    private String role;
    private boolean emailVerified;
    private LocalDateTime createdAt;
}
