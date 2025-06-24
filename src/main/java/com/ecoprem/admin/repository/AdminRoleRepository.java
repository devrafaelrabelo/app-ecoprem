package com.ecoprem.admin.repository;

import com.ecoprem.entity.security.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminRoleRepository extends JpaRepository<Role, UUID> {
    @EntityGraph(attributePaths = "permissions")
    Optional<Role> findById(UUID id);
    boolean existsByNameIgnoreCase(String name);

}