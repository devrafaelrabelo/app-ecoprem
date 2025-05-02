package com.ecoprem.auth.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

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
