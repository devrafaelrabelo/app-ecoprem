package com.ecoprem.resource.repository;

import com.ecoprem.entity.common.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {
    // Métodos customizados podem ser adicionados aqui no futuro
}
