package com.controlcenter.entity.resource;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


import java.util.UUID;

@Data
@Entity
@Table(name = "resource_status", schema = "resource")
public class ResourceStatus {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code; // Ex: "DISPONIVEL", "ALOCADO"

    @Column(nullable = false)
    private String name; // Ex: "Disponível", "Em uso"

    private String description;

    @Column(nullable = false)
    private boolean blocksAllocation = false; // Pode alocar se estiver nesse status?
}