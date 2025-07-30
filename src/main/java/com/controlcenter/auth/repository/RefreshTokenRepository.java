package com.controlcenter.auth.repository;

import com.controlcenter.entity.auth.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Transactional
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByUserIdAndSessionId(UUID userId, String sessionId);
    void deleteByUserIdAndSessionId(UUID userId, String sessionId);
    @Modifying
    void deleteByUserId(UUID userId);
    Optional<RefreshToken> findByToken(String token);
    @EntityGraph(attributePaths = {
            "user", "user.roles", "user.status"
    })
    Optional<RefreshToken> findByTokenAndSessionId(String token, String sessionId);

}
