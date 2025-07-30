package com.controlcenter.auth.repository;

import com.controlcenter.entity.auth.BackupCode;
import com.controlcenter.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BackupCodeRepository extends JpaRepository<BackupCode, UUID> {
    List<BackupCode> findByUser(User user);
    Optional<BackupCode> findByUserAndCodeAndUsedFalse(User user, String code);
}
