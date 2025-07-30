package com.controlcenter.auth.repository;

import com.controlcenter.entity.auth.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {
    List<ActivityLog> findByUserId(UUID userId);
    List<ActivityLog> findByUserIdOrderByActivityDateDesc(UUID userId);
}
