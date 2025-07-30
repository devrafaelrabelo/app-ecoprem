package com.controlcenter.admin.repository;

import com.controlcenter.entity.security.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminPermissionRepository extends JpaRepository<Permission, UUID> {
    Optional<Permission> findByName(String name);
    boolean existsByName(String name);
    boolean existsByNameIgnoreCase(String name);
    List<Permission> findByNameContainingIgnoreCase(String name);

}