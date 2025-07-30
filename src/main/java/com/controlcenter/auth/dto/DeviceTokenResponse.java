package com.controlcenter.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeviceTokenResponse {
    private UUID id;
    private String deviceName;
    private String token;
    private LocalDateTime createdAt;
}
