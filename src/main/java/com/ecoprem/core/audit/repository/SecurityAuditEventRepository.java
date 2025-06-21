package com.ecoprem.core.audit.repository;

import com.ecoprem.core.audit.entity.SecurityAuditEvent;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface SecurityAuditEventRepository extends JpaRepository<SecurityAuditEvent, UUID>,
        JpaSpecificationExecutor<SecurityAuditEvent> {

    List<SecurityAuditEvent> findByUserId(UUID userId);
}
