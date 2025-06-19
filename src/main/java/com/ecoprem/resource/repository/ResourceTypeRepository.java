package com.ecoprem.resource.repository;

import com.ecoprem.entity.common.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ResourceTypeRepository extends JpaRepository<ResourceType, UUID> {
    boolean existsByCodeIgnoreCase(String code);
}