package com.ecoprem.auth.dto;

import lombok.Data;

@Data
public class DeviceTokenRequest {
    private String deviceName;
    private String token;
}
