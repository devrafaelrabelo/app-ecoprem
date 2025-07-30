package com.controlcenter.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SessionInfoDTO {
    private String sessionId;
    private UUID userId;
    private String username;
    private String ipAddress;
    private String browser;
    private String device;
    private String operatingSystem;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessAt;
    private LocalDateTime expiresAt;
    private boolean expired;
}
