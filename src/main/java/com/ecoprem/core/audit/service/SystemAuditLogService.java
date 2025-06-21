package com.ecoprem.core.audit.service;

import com.ecoprem.core.audit.dto.SystemAuditLogDTO;
import com.ecoprem.core.audit.repository.SystemAuditLogRepository;
import com.ecoprem.entity.audit.SystemAuditLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SystemAuditLogService {

    private final SystemAuditLogRepository repository;
    private final ObjectMapper objectMapper;

    public void logAction(String action, String targetEntity, String targetId,
                          String performedBy, UUID performedById,
                          HttpServletRequest request, Object details) {
        try {
            String jsonDetails = (details != null) ? objectMapper.writeValueAsString(details) : null;

            SystemAuditLog log = SystemAuditLog.builder()
                    .action(action)
                    .targetEntity(targetEntity)
                    .targetId(targetId)
                    .performedBy(performedBy)
                    .performedById(performedById)
                    .ipAddress(request.getRemoteAddr())
                    .userAgent(request.getHeader("User-Agent"))
                    .httpMethod(request.getMethod())
                    .path(request.getRequestURI())
                    .sessionId(request.getSession(false) != null ? request.getSession().getId() : null)
                    .details(jsonDetails)
                    .build();

            repository.save(log);
        } catch (Exception e) {
            // Logar internamente, mas não quebrar a execução
            System.err.println("Erro ao registrar auditoria: " + e.getMessage());
        }
    }

    public Page<SystemAuditLogDTO> search(String action, String targetEntity, String targetId, String performedBy,
                                          LocalDateTime start, LocalDateTime end, Pageable pageable) {
        // Aqui usaremos Specification ou um filtro customizável, posso montar se quiser.
        return repository.findAll(pageable).map(this::toDTO);
    }

    private SystemAuditLogDTO toDTO(SystemAuditLog log) {
        return SystemAuditLogDTO.builder()
                .id(log.getId())
                .action(log.getAction())
                .targetEntity(log.getTargetEntity())
                .targetId(log.getTargetId())
                .performedBy(log.getPerformedBy())
                .performedById(log.getPerformedById())
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .httpMethod(log.getHttpMethod())
                .path(log.getPath())
                .sessionId(log.getSessionId())
                .details(log.getDetails())
                .timestamp(log.getTimestamp())
                .build();
    }
}
