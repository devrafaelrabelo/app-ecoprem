package com.controlcenter.resource.repository;

import com.controlcenter.entity.communication.InternalExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InternalExtensionRepository extends JpaRepository<InternalExtension, UUID> {
    // Não precisa de query manual
}
