package com.controlcenter.auth.repository;

import com.controlcenter.entity.security.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserPermissionRepository extends JpaRepository<UserPermission, UUID> {

    void deleteByUserIdAndPermissionId(UUID userId, UUID permissionId);

    boolean existsByUserIdAndPermissionId(UUID userId, UUID permissionId);

    Optional<UserPermission> findByUserIdAndPermissionId(UUID userId, UUID permissionId);

    List<UserPermission> findByUserId(UUID userId);
}