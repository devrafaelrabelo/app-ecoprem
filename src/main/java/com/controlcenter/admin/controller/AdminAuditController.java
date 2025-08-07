package com.controlcenter.admin.controller;

import com.controlcenter.core.audit.dto.RequestAuditLogDTO;
import com.controlcenter.core.audit.dto.SecurityAuditEventDTO;
import com.controlcenter.core.audit.dto.SystemAuditLogDTO;
import com.controlcenter.core.audit.service.RequestAuditLogService;
import com.controlcenter.core.audit.service.SecurityAuditEventService;
import com.controlcenter.core.audit.service.SystemAuditLogService;
import com.controlcenter.exceptions.exception.InvalidDateRangeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/audits")
@RequiredArgsConstructor
public class AdminAuditController {

    private final SecurityAuditEventService securityAuditEventService;
    private final RequestAuditLogService requestAuditLogService;
    private final SystemAuditLogService systemAuditLogService;

    private void validateDateRange(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new InvalidDateRangeException("A data final não pode ser anterior à data inicial.");
        }
    }

    private void validateAllowedParams(HttpServletRequest request, Set<String> allowedParams) {
        for (String param : request.getParameterMap().keySet()) {
            if (!allowedParams.contains(param)) {
                throw new IllegalArgumentException("Parâmetro não suportado: " + param);
            }
        }
    }


    @Operation(
            summary = "Listar eventos de auditoria de segurança",
            description = "Retorna os registros de eventos de segurança como tentativas de acesso negado, logins suspeitos, entre outros.",
            parameters = {
                    @Parameter(name = "eventType", description = "Tipo do evento (ex: ACCESS_DENIED, LOGIN_FAILED, etc.)", example = "ACCESS_DENIED"),
                    @Parameter(name = "username", description = "Username do usuário (filtro parcial, sem case sensitive)", example = "admin"),
                    @Parameter(name = "startDate", description = "Data inicial no formato ISO", example = "2025-01-01T00:00:00"),
                    @Parameter(name = "endDate", description = "Data final no formato ISO", example = "2025-12-31T23:59:59")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de eventos retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou datas inconsistentes"),
            @ApiResponse(responseCode = "403", description = "Acesso negado ao recurso"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao buscar os eventos")
    })
    @GetMapping("/security-events")
    @PreAuthorize("hasAuthority('audit:read')")
    public Page<SecurityAuditEventDTO> listAuditEvents(
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable,
            HttpServletRequest request
    ) {
        validateAllowedParams(request, Set.of("eventType", "username", "startDate", "endDate", "page", "size", "sort"));
        validateDateRange(startDate, endDate);
        return securityAuditEventService.search(eventType, username, startDate, endDate, pageable);
    }

    @Operation(
            summary = "Listar logs de requisições HTTP auditadas",
            description = "Retorna os registros de requisições HTTP capturadas pelo sistema, incluindo método, IP, rota acessada, status, usuário e tempo de execução.",
            parameters = {
                    @Parameter(name = "path", description = "Rota acessada (filtro parcial, ex: /api/auth)", example = "/api/auth"),
                    @Parameter(name = "ip", description = "Endereço IP de origem", example = "192.168.0.1"),
                    @Parameter(name = "method", description = "Método HTTP (GET, POST, etc.)", example = "GET"),
                    @Parameter(name = "username", description = "Username do usuário autenticado", example = "admin"),
                    @Parameter(name = "status", description = "Código de status da resposta", example = "200"),
                    @Parameter(name = "start", description = "Data/hora inicial no formato ISO", example = "2025-01-01T00:00:00"),
                    @Parameter(name = "end", description = "Data/hora final no formato ISO", example = "2025-12-31T23:59:59")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de logs retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou datas inconsistentes"),
            @ApiResponse(responseCode = "403", description = "Acesso negado ao recurso"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao buscar os registros")
    })
    @GetMapping("/request-events")
    @PreAuthorize("hasAuthority('audit:read')")
    public Page<RequestAuditLogDTO> listRequestLogs(
            @RequestParam(required = false) String path,
            @RequestParam(required = false) String ip,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            Pageable pageable,
            HttpServletRequest request
    ) {
        validateAllowedParams(request, Set.of("path", "ip", "method", "username", "status", "start", "end", "page", "size", "sort"));
        validateDateRange(start, end);
        return requestAuditLogService.search(path, ip, method, username, status, start, end, pageable);
    }


    @Operation(
            summary = "Listar auditoria de ações administrativas",
            description = "Retorna ações sensíveis realizadas no sistema, como atribuição de permissões, alterações de usuários, recursos corporativos e mudanças administrativas.",
            parameters = {
                    @Parameter(name = "action", description = "Ação realizada (ex: CREATE, UPDATE, DELETE)", example = "CREATE"),
                    @Parameter(name = "targetEntity", description = "Entidade alvo da ação (ex: USER, ROLE, COMPANY)", example = "USER"),
                    @Parameter(name = "targetId", description = "ID do alvo da ação", example = "e6e28546-5601-4840-9804-f61ea2e55c4a"),
                    @Parameter(name = "performedBy", description = "Usuário que realizou a ação", example = "admin"),
                    @Parameter(name = "start", description = "Data/hora inicial no formato ISO", example = "2025-01-01T00:00:00"),
                    @Parameter(name = "end", description = "Data/hora final no formato ISO", example = "2025-12-31T23:59:59")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de eventos retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou datas inconsistentes"),
            @ApiResponse(responseCode = "403", description = "Acesso negado ao recurso"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao buscar os eventos")
    })
    @GetMapping("/system-events")
    @PreAuthorize("hasAuthority('audit:read')")
    public Page<SystemAuditLogDTO> listSystemEvents(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String targetEntity,
            @RequestParam(required = false) String targetId,
            @RequestParam(required = false) String performedBy,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            Pageable pageable,
            HttpServletRequest request
    ) {
        validateAllowedParams(request, Set.of("action", "targetEntity", "targetId", "performedBy", "start", "end", "page", "size", "sort"));
        validateDateRange(start, end);
        return systemAuditLogService.search(action, targetEntity, targetId, performedBy, start, end, pageable);
    }
}
