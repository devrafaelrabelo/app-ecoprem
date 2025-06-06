package com.ecoprem.auth.repository;

import com.ecoprem.entity.auth.Pending2FALogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface Pending2FALoginRepository extends JpaRepository<Pending2FALogin, UUID> {
    Optional<Pending2FALogin> findByTempToken(String tempToken);
}
