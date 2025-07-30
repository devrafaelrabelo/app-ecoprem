package com.controlcenter.core.audit.repository;

import com.controlcenter.entity.audit.SecurityAuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface SecurityAuditEventRepository extends JpaRepository<SecurityAuditEvent, UUID>,
        JpaSpecificationExecutor<SecurityAuditEvent> {

    List<SecurityAuditEvent> findByUserId(UUID userId);
}
