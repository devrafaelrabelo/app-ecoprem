package com.controlcenter.core.audit.service;

import com.controlcenter.core.audit.dto.SystemAuditLogDTO;
import com.controlcenter.core.audit.repository.SystemAuditLogRepository;
import com.controlcenter.exceptions.exception.InvalidDateRangeException;
import com.controlcenter.entity.audit.SystemAuditLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
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
            log.warn("Erro ao registrar auditoria do sistema: {}", e.getMessage(), e);
        }
    }

    public Page<SystemAuditLogDTO> search(String action, String targetEntity, String targetId, String performedBy,
                                          LocalDateTime start, LocalDateTime end, Pageable pageable) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new InvalidDateRangeException("A data final não pode ser anterior à data inicial.");
        }

        Specification<SystemAuditLog> spec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (action != null && !action.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("action"), action));
            }

            if (targetEntity != null && !targetEntity.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("targetEntity"), targetEntity));
            }

            if (targetId != null && !targetId.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("targetId"), targetId));
            }

            if (performedBy != null && !performedBy.isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("performedBy")), "%" + performedBy.toLowerCase() + "%"));
            }

            if (start != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("timestamp"), start));
            }

            if (end != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("timestamp"), end));
            }

            return predicate;
        };

        try {
            return repository.findAll(spec, pageable).map(this::toDTO);
        } catch (Exception e) {
            log.error("Erro ao buscar registros de auditoria do sistema", e);
            throw new RuntimeException("Erro ao buscar registros de auditoria. Tente novamente ou contate o suporte.");
        }
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
