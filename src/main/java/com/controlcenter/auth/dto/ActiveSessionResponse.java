package com.controlcenter.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informações de uma sessão ativa do usuário.")
public class ActiveSessionResponse {

    @Schema(description = "ID da sessão", example = "9d6f1b62-94e6-4a4c-bd60-2e4a6f2acb6c")
    private UUID sessionId;

    @Schema(description = "Dispositivo utilizado", example = "Desktop")
    private String device;

    @Schema(description = "Nome amigável do dispositivo", example = "Chrome on Windows")
    private String deviceName;

    @Schema(description = "Navegador utilizado", example = "Chrome")
    private String browser;

    @Schema(description = "Sistema operacional", example = "Windows 11")
    private String operatingSystem;

    @Schema(description = "Endereço IP", example = "192.168.1.25")
    private String ipAddress;

    @Schema(description = "Data e hora de criação da sessão")
    private LocalDateTime createdAt;

    @Schema(description = "Data e hora de expiração da sessão")
    private LocalDateTime expiresAt;
}

