package com.ecoprem.auth.repository;

import com.ecoprem.auth.entity.AccessLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccessLevelRepository extends JpaRepository<AccessLevel, UUID> {
    Optional<AccessLevel> findByName(String name);
}
