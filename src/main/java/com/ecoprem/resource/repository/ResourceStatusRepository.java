package com.ecoprem.resource.repository;

import com.ecoprem.entity.common.ResourceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ResourceStatusRepository extends JpaRepository<ResourceStatus, UUID> {
    boolean existsByCodeIgnoreCase(String code);
}
