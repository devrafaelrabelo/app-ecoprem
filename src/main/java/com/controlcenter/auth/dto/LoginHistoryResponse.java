package com.controlcenter.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginHistoryResponse {
    private UUID id;
    private LocalDateTime loginDate;
    private String ipAddress;
    private String location;  // podemos deixar null por enquanto
    private String device;
    private String browser;
    private String operatingSystem;
    private boolean success;
    private String failureReason; // opcional, só preenchido se success == false

}
