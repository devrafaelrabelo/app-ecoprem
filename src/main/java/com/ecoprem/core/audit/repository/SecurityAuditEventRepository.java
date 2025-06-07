package com.ecoprem.core.audit.repository;

import com.ecoprem.core.audit.entity.SecurityAuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SecurityAuditEventRepository extends JpaRepository<SecurityAuditEvent, UUID> {
    List<SecurityAuditEvent> findByUserId(UUID userId);
}
