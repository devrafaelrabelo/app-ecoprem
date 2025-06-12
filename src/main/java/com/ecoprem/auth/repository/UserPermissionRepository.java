package com.ecoprem.auth.repository;

import com.ecoprem.entity.security.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserPermissionRepository extends JpaRepository<UserPermission, UUID> {
    void deleteByUserIdAndPermissionId(UUID userId, UUID permissionId);
}