package com.controlcenter.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SessionRevokeAuditDTO {
    private UUID sessionId;
    private UUID userId;
    private String username;
    private String reason;           // novo
    private String revokedBy;        // novo
    private LocalDateTime revokedAt;
}