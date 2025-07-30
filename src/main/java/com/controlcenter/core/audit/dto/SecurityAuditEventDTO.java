package com.controlcenter.core.audit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Informações de um evento de auditoria de segurança.")
public class SecurityAuditEventDTO {

    @Schema(description = "ID único do evento de auditoria", example = "b2b3d2e5-1234-4cde-9abc-998877665544")
    private UUID id;

    @Schema(description = "Nome de usuário associado ao evento", example = "rafael")
    private String username;

    @Schema(description = "Tipo do evento de segurança", example = "ACCESS_DENIED")
    private String eventType;

    @Schema(description = "Descrição detalhada do evento", example = "Tentativa de acessar recurso sem permissão")
    private String description;

    @Schema(description = "Endpoint acessado", example = "/api/admin/users")
    private String path;

    @Schema(description = "Método HTTP utilizado", example = "GET")
    private String method;

    @Schema(description = "Endereço IP de origem", example = "192.168.0.10")
    private String ipAddress;

    @Schema(description = "User-Agent do cliente", example = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
    private String userAgent;

    @Schema(description = "Data e hora do evento", example = "2025-06-21T09:30:21")
    private LocalDateTime timestamp;
}
