package com.controlcenter.core.audit.repository;


import com.controlcenter.entity.audit.SystemAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SystemAuditLogRepository extends JpaRepository<SystemAuditLog, UUID>, JpaSpecificationExecutor<SystemAuditLog> {
}
