package com.controlcenter.core.audit.repository;

import com.controlcenter.entity.audit.RequestAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RequestAuditLogRepository extends JpaRepository<RequestAuditLog, UUID>,
        JpaSpecificationExecutor<RequestAuditLog> {
}
