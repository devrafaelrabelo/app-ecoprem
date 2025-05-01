package com.ecoprem.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BackupCodeResponse {
    private UUID id;
    private String code;
    private boolean used;
    private LocalDateTime createdAt;
    private LocalDateTime usedAt;
}
