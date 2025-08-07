package com.controlcenter.core.audit.service;

import com.controlcenter.core.audit.dto.RequestAuditLogDTO;
import com.controlcenter.core.audit.repository.RequestAuditLogRepository;
import com.controlcenter.exceptions.exception.InvalidDateRangeException;
import com.controlcenter.entity.audit.RequestAuditLog;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestAuditLogService {

    private final RequestAuditLogRepository requestAuditLogRepository;

    public Page<RequestAuditLogDTO> search(
            String path,
            String ip,
            String method,
            String username,
            Integer status,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    ) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new InvalidDateRangeException("A data final não pode ser anterior à data inicial.");
        }

        Specification<RequestAuditLog> spec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (path != null && !path.isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("path")), "%" + path.toLowerCase() + "%"));
            }

            if (ip != null && !ip.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("ipAddress"), ip));
            }

            if (method != null && !method.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("method"), method));
            }

            if (username != null && !username.isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%"));
            }

            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("statusCode"), status));
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
            return requestAuditLogRepository.findAll(spec, pageable).map(this::toDTO);
        } catch (Exception ex) {
            log.error("Erro ao buscar logs de requisições auditadas", ex);
            throw new RuntimeException("Erro ao buscar logs de requisições. Tente novamente ou contate o suporte.");
        }
    }

    private RequestAuditLogDTO toDTO(RequestAuditLog log) {
        return RequestAuditLogDTO.builder()
                .id(log.getId())
                .method(log.getMethod())
                .path(log.getPath())
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .statusCode(log.getStatusCode())
                .username(log.getUsername())
                .userId(log.getUserId())
                .durationMs(log.getDurationMs())
                .timestamp(log.getTimestamp())
                .build();
    }
}
