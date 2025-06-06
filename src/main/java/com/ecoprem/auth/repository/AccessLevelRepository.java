package com.ecoprem.auth.repository;

import com.ecoprem.entity.security.AccessLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccessLevelRepository extends JpaRepository<AccessLevel, UUID> {
    Optional<AccessLevel> findByName(String name);
}
