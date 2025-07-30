package com.controlcenter.core.audit.service;

import com.controlcenter.core.audit.dto.SecurityAuditEventDTO;
import com.controlcenter.entity.audit.SecurityAuditEvent;
import com.controlcenter.core.audit.repository.SecurityAuditEventRepository;
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
public class SecurityAuditEventService {

    private final SecurityAuditEventRepository securityAuditEventRepository;

    public Page<SecurityAuditEventDTO> search(
            String eventType,
            String username,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    ) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("A data final não pode ser anterior à data inicial.");
        }

        Specification<SecurityAuditEvent> spec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (eventType != null && !eventType.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("eventType"), eventType));
            }

            if (username != null && !username.isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%"));
            }

            if (startDate != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("timestamp"), startDate));
            }

            if (endDate != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("timestamp"), endDate));
            }

            return predicate;
        };

        try {
            return securityAuditEventRepository.findAll(spec, pageable).map(this::toDTO);
        } catch (Exception ex) {
            log.error("Erro ao buscar eventos de segurança auditados", ex);
            throw new RuntimeException("Erro ao buscar eventos de segurança auditados. Tente novamente ou contate o suporte.");
        }
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
