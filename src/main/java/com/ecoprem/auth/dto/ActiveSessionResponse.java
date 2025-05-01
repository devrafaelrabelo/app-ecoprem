package com.ecoprem.auth.dto;

import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
public class ActiveSessionResponse {
    private UUID id;
    private String sessionId;
    private String device;
    private String browser;
    private String operatingSystem;
    private String ipAddress;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
