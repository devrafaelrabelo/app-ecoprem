package com.ecoprem.auth.repository;

import com.ecoprem.auth.entity.BackupCode;
import com.ecoprem.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BackupCodeRepository extends JpaRepository<BackupCode, UUID> {
    List<BackupCode> findByUser(User user);
    Optional<BackupCode> findByUserAndCodeAndUsedFalse(User user, String code);
}
