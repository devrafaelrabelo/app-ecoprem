package com.controlcenter.auth.repository;

import com.controlcenter.entity.auth.ActiveSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActiveSessionRepository extends JpaRepository<ActiveSession, UUID> {
    List<ActiveSession> findByUserId(UUID userId);
    void deleteBySessionId(String sessionId);
    void deleteByUserId(UUID userId);
    Optional<ActiveSession> findBySessionId(String sessionId);
    List<ActiveSession> findByExpiresAtBefore(LocalDateTime time);
    Optional<ActiveSession> findTopByUserIdOrderByCreatedAtDesc(UUID userId);

    @Query("""
    SELECT s FROM ActiveSession s
    WHERE (:userId IS NULL OR s.user.id = :userId)
      AND (:ipAddress IS NULL OR s.ipAddress ILIKE %:ipAddress%)
      AND (:device IS NULL OR s.device ILIKE %:device%)
      AND (:expired IS NULL OR
           (:expired = true AND s.expiresAt < CURRENT_TIMESTAMP) OR
           (:expired = false AND s.expiresAt >= CURRENT_TIMESTAMP))
""")
    List<ActiveSession> findMatching(@Param("userId") UUID userId,
                                     @Param("ipAddress") String ipAddress,
                                     @Param("device") String device,
                                     @Param("expired") Boolean expired);

}
