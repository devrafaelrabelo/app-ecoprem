package com.controlcenter.resource.repository;

import com.controlcenter.entity.resource.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ResourceRepository extends JpaRepository<Resource, UUID> {

    // Filtrar por código do status (ex: DISPONIVEL, ALOCADO)
    Page<Resource> findByStatus_Code(String code, Pageable pageable);

    // Busca por nome, patrimônio ou serial
    @Query("""
    SELECT r FROM Resource r
    WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :text, '%'))
       OR LOWER(r.assetTag) LIKE LOWER(CONCAT('%', :text, '%'))
       OR LOWER(r.serialNumber) LIKE LOWER(CONCAT('%', :text, '%'))
""")
    Page<Resource> searchByText(@Param("text") String text, Pageable pageable);
}