package com.controlcenter.auth.repository;

import com.controlcenter.entity.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(String name); // ← esse método precisa existir
    List<Role> findByNameIn(List<String> names); // ← esse método precisa existir
}
