package com.controlcenter.admin.dto;

import lombok.Data;

@Data
public class SessionFilterRequest {
    private String ipAddress;
    private String browser;
    private String operatingSystem;
    private String device;
    private Boolean expired;
    private String username; // caso queira filtrar por usuário também
}