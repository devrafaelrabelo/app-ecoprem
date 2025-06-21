package com.ecoprem.admin.controller;

import com.ecoprem.admin.dto.SecurityAuditEventDTO;
import com.ecoprem.core.audit.service.SecurityAuditEventService;
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

    private final SecurityAuditEventService auditService;

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
        return auditService.search(eventType, username, startDate, endDate, pageable);
    }
}


