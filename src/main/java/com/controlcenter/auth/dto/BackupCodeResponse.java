package com.controlcenter.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Schema(description = "Representação de um código de backup 2FA")
public class BackupCodeResponse {

    @Schema(description = "Identificador do código", example = "9f6e4c9d-80f4-4b42-8ad6-6c313e4cabea")
    private UUID id;

    @Schema(description = "Código de backup", example = "ABCDEFGH")
    private String code;

    @Schema(description = "Indica se já foi usado", example = "false")
    private boolean used;

    @Schema(description = "Data de criação", example = "2024-06-05T10:23:00")
    private LocalDateTime createdAt;

    @Schema(description = "Data em que foi usado (se aplicável)", example = "2024-07-01T08:45:00")
    private LocalDateTime usedAt;
}