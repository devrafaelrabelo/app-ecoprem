package com.ecoprem.core.audit.service;

import com.ecoprem.core.audit.dto.RequestAuditLogDTO;
import com.ecoprem.core.audit.repository.RequestAuditLogRepository;
import com.ecoprem.entity.audit.RequestAuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
        Specification<RequestAuditLog> spec = Specification.where(null);

        if (path != null && !path.isBlank()) {
            spec = spec.and((r, q, cb) -> cb.like(cb.lower(r.get("path")), "%" + path.toLowerCase() + "%"));
        }
        if (ip != null && !ip.isBlank()) {
            spec = spec.and((r, q, cb) -> cb.equal(r.get("ipAddress"), ip));
        }
        if (method != null && !method.isBlank()) {
            spec = spec.and((r, q, cb) -> cb.equal(r.get("method"), method));
        }
        if (username != null && !username.isBlank()) {
            spec = spec.and((r, q, cb) -> cb.like(cb.lower(r.get("username")), "%" + username.toLowerCase() + "%"));
        }
        if (status != null) {
            spec = spec.and((r, q, cb) -> cb.equal(r.get("statusCode"), status));
        }
        if (start != null) {
            spec = spec.and((r, q, cb) -> cb.greaterThanOrEqualTo(r.get("timestamp"), start));
        }
        if (end != null) {
            spec = spec.and((r, q, cb) -> cb.lessThanOrEqualTo(r.get("timestamp"), end));
        }

        return requestAuditLogRepository.findAll(spec, pageable).map(this::toDTO);
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
                .userId(log.getUserId()) // novo campo
                .durationMs(log.getDurationMs())
                .timestamp(log.getTimestamp())
                .build();
    }
}

