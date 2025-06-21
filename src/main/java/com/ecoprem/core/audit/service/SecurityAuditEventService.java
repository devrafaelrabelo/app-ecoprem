package com.ecoprem.core.audit.service;


import com.ecoprem.core.audit.dto.SecurityAuditEventDTO;
import com.ecoprem.entity.audit.SecurityAuditEvent;
import com.ecoprem.core.audit.repository.SecurityAuditEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SecurityAuditEventService {

    private final SecurityAuditEventRepository securityAuditEventRepository;

    public Page<SecurityAuditEventDTO> search(
            String eventType,
            String username,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    ) {
        Specification<SecurityAuditEvent> spec = Specification.where(null);

        if (eventType != null && !eventType.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("eventType"), eventType));
        }

        if (username != null && !username.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%"));
        }

        if (startDate != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("timestamp"), startDate));
        }

        if (endDate != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("timestamp"), endDate));
        }

        return securityAuditEventRepository.findAll(spec, pageable).map(this::toDTO);
    }

    private SecurityAuditEventDTO toDTO(SecurityAuditEvent event) {
        return SecurityAuditEventDTO.builder()
                .id(event.getId())
                .username(event.getUsername())
                .eventType(event.getEventType())
                .description(event.getDescription())
                .path(event.getPath())
                .method(event.getMethod())
                .ipAddress(event.getIpAddress())
                .userAgent(event.getUserAgent())
                .timestamp(event.getTimestamp())
                .build();
    }
}
