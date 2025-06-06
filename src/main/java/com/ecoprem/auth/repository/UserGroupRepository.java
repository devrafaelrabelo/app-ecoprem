package com.ecoprem.auth.repository;

import com.ecoprem.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserGroupRepository extends JpaRepository<UserGroup, UUID> {
    Optional<UserGroup> findByName(String name);
}
