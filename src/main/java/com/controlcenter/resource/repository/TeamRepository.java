package com.controlcenter.resource.repository;

import com.controlcenter.entity.common.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {
    // Métodos customizados podem ser adicionados aqui no futuro
}
