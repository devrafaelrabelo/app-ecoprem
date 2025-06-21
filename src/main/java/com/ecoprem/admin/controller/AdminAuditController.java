package com.ecoprem.admin.controller;


import com.ecoprem.core.audit.dto.RequestAuditLogDTO;
import com.ecoprem.core.audit.dto.SecurityAuditEventDTO;
import com.ecoprem.core.audit.dto.SystemAuditLogDTO;
import com.ecoprem.core.audit.service.RequestAuditLogService;
import com.ecoprem.core.audit.service.SecurityAuditEventService;
import com.ecoprem.core.audit.service.SystemAuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/audits")
@RequiredArgsConstructor
public class AdminAuditController {

    private final SecurityAuditEventService securityAuditEventService;
    private final RequestAuditLogService requestAuditLogService;
    private final SystemAuditLogService systemAuditLogService;

    @PreAuthorize("hasAuthority('audit:view')")
    @Operation(
            summary = "Listar eventos de auditoria de segurança",
            description = "Retorna os registros de eventos de segurança como tentativas de acesso negado, login suspeito, entre outros. Permite filtros por tipo de evento, nome de usuário e intervalo de datas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de eventos retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado ao recurso")
    })
    @GetMapping("/security-events")
    public Page<SecurityAuditEventDTO> listAuditEvents(
            @Parameter(description = "Tipo do evento (ex: ACCESS_DENIED, LOGIN_FAILED, etc.)")
            @RequestParam(required = false) String eventType,

            @Parameter(description = "Username do usuário (filtro parcial, sem case sensitive)")
            @RequestParam(required = false) String username,

            @Parameter(description = "Data inicial no formato ISO (ex: 2025-06-21T00:00:00)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

            @Parameter(description = "Data final no formato ISO (ex: 2025-06-21T23:59:59)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,

            @Parameter(hidden = true) Pageable pageable
    ) {
        return securityAuditEventService.search(eventType, username, startDate, endDate, pageable);
    }

    @Operation(
            summary = "Listar logs de requisições HTTP auditadas",
            description = "Retorna os registros de requisições HTTP capturados pelo sistema, incluindo método, IP, rota acessada, status, usuário e tempo de execução."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de logs retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado ao recurso")
    })
    @GetMapping("/request-events")
    @PreAuthorize("hasAuthority('audit:view')")
    public Page<RequestAuditLogDTO> listRequestLogs(
            @Parameter(description = "Rota acessada (filtro parcial, ex: /api/auth)")
            @RequestParam(required = false) String path,

            @Parameter(description = "Endereço IP de origem")
            @RequestParam(required = false) String ip,

            @Parameter(description = "Método HTTP (GET, POST, etc.)")
            @RequestParam(required = false) String method,

            @Parameter(description = "Username do usuário autenticado")
            @RequestParam(required = false) String username,

            @Parameter(description = "Código de status da resposta (ex: 200, 403, 500)")
            @RequestParam(required = false) Integer status,

            @Parameter(description = "Data/hora inicial no formato ISO (ex: 2025-06-21T00:00:00)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,

            @Parameter(description = "Data/hora final no formato ISO (ex: 2025-06-21T23:59:59)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,

            @Parameter(hidden = true) Pageable pageable
    ) {
        return requestAuditLogService.search(path, ip, method, username, status, start, end, pageable);
    }

    @GetMapping("/system-events")
    @PreAuthorize("hasAuthority('audit:view')")
    @Operation(
            summary = "Listar auditoria de ações administrativas",
            description = "Retorna ações sensíveis realizadas no sistema, como atribuição de permissões, alterações de usuários, recursos corporativos e mudanças administrativas."
    )
    public Page<SystemAuditLogDTO> listSystemEvents(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String targetEntity,
            @RequestParam(required = false) String targetId,
            @RequestParam(required = false) String performedBy,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            Pageable pageable
    ) {
        return systemAuditLogService.search(action, targetEntity, targetId, performedBy, start, end, pageable);
    }
}


