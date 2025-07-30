package com.controlcenter.admin.repository;

import com.controlcenter.admin.dto.AdminRoleWithCountDTO;
import com.controlcenter.entity.security.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminRoleRepository extends JpaRepository<Role, UUID> {
    @EntityGraph(attributePaths = "permissions")
    Optional<Role> findById(UUID id);
    boolean existsByNameIgnoreCase(String name);

    @Query("""
    SELECT new com.controlcenter.admin.dto.AdminRoleWithCountDTO(
        r.id,
        r.name,
        r.description,
        r.systemRole,
        COUNT(p)
    )
    FROM Role r
    LEFT JOIN r.permissions p
    GROUP BY r.id, r.name, r.description, r.systemRole
""")
    List<AdminRoleWithCountDTO> findAllWithPermissionCount();

}