package com.controlcenter.resource.repository;

import com.controlcenter.entity.resource.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ResourceTypeRepository extends JpaRepository<ResourceType, UUID> {
    boolean existsByCodeIgnoreCase(String code);
}