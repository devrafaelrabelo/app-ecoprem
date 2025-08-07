package com.controlcenter.common.repository;

import com.controlcenter.entity.common.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PositionRepository extends JpaRepository<Position, UUID> {
    Optional<Position> findByName(String name);
    boolean existsByName(String name);
    List<Position> findByNameContainingIgnoreCase(String name);
    List<Position> findByDescriptionContainingIgnoreCase(String description);
    List<Position> findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCase(String name, String description);
}