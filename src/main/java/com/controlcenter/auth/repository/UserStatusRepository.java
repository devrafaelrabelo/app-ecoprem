package com.controlcenter.auth.repository;

import com.controlcenter.entity.security.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {
    Optional<UserStatus> findByName(String name); // ← Correto agora
}