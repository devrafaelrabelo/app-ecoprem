package com.controlcenter.resource.repository;

import com.controlcenter.entity.resource.ResourceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ResourceStatusRepository extends JpaRepository<ResourceStatus, UUID> {
    boolean existsByCodeIgnoreCase(String code);
}
