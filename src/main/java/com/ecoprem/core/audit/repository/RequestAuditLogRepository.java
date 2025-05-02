package com.ecoprem.core.audit.repository;

import com.ecoprem.core.audit.entity.RequestAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestAuditLogRepository extends JpaRepository<RequestAuditLog, Long> {
}
