package com.controlcenter.admin.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessionDTO(
        UUID id,
        String sessionId,
        String device,
        String browser,
        String operatingSystem,
        String ipAddress,
        LocalDateTime createdAt,
        LocalDateTime lastAccessAt,
        LocalDateTime expiresAt
) {}
