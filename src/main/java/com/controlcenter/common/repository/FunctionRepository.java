package com.controlcenter.common.repository;

import com.controlcenter.entity.common.Function;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FunctionRepository extends JpaRepository<Function, UUID> {
    List<Function> findByDepartmentId(UUID departmentId);

    @EntityGraph(attributePaths = {"department"})
    Optional<Function> findWithDepartmentById(UUID id);

    @EntityGraph(attributePaths = {"department"})
    List<Function> findAll(); // carrega Department junto
}