package com.ecoprem.auth.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ActiveSessionResponse {
    private UUID sessionId;
    private String device;
    private String deviceName;  // 🚀 NOVO CAMPO
    private String browser;
    private String operatingSystem;
    private String ipAddress;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
