package com.ecoprem.auth.repository;

import com.ecoprem.auth.entity.ActiveSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActiveSessionRepository extends JpaRepository<ActiveSession, UUID> {
    List<ActiveSession> findByUserId(UUID userId);
    void deleteBySessionId(String sessionId);
}
