package com.ecoprem.auth.dto;

import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
public class DeviceTokenResponse {
    private UUID id;
    private String deviceName;
    private String token;
    private LocalDateTime createdAt;
}
