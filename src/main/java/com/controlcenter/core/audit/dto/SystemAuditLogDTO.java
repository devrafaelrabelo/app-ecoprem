package com.controlcenter.core.audit.dto;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record SystemAuditLogDTO(
        UUID id,
        String action,
        String targetEntity,
        String targetId,
        String performedBy,
        UUID performedById,
        String ipAddress,
        String userAgent,
        String httpMethod,
        String path,
        String sessionId,
        String details,
        LocalDateTime timestamp
) {}
