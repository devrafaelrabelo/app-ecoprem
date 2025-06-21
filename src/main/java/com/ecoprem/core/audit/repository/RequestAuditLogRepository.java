package com.ecoprem.core.audit.repository;

import com.ecoprem.entity.audit.RequestAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestAuditLogRepository extends JpaRepository<RequestAuditLog, Long> {
}
