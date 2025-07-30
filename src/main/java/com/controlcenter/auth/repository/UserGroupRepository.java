package com.controlcenter.auth.repository;

import com.controlcenter.entity.user.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserGroupRepository extends JpaRepository<UserGroup, UUID> {
    Optional<UserGroup> findByName(String name);
}
