package com.controlcenter.auth.repository;

import com.controlcenter.entity.auth.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, UUID> {
}
