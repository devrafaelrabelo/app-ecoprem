package com.ecoprem.auth.repository;

import com.ecoprem.entity.auth.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, UUID> {
    Optional<RevokedToken> findByToken(String token);
    boolean existsByToken(String token);
    void deleteAllByExpiresAtBefore(LocalDateTime now);
}
