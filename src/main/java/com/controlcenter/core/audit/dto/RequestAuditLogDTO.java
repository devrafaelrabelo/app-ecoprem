package com.controlcenter.core.audit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Registro de requisição HTTP auditada no sistema.")
public class RequestAuditLogDTO {

    @Schema(description = "ID do log", example = "125")
    private Long id;

    @Schema(description = "Método HTTP usado na requisição", example = "GET")
    private String method;

    @Schema(description = "Caminho da requisição", example = "/api/auth/login")
    private String path;

    @Schema(description = "Endereço IP de origem", example = "192.168.0.1")
    private String ipAddress;

    @Schema(description = "User-Agent do cliente", example = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
    private String userAgent;

    @Schema(description = "Status HTTP da resposta", example = "200")
    private int statusCode;

    @Schema(description = "Username do usuário autenticado", example = "rafael")
    private String username;

    @Schema(description = "UUID do usuário autenticado", example = "8d9e8d9f-92ab-a5b7-ff6c-889900112233")
    private UUID userId;

    @Schema(description = "Tempo de execução da requisição (ms)", example = "123")
    private Integer durationMs;

    @Schema(description = "Data e hora da requisição", example = "2025-06-21T09:30:21")
    private LocalDateTime timestamp;
}
