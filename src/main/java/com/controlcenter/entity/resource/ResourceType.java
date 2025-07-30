package com.controlcenter.entity.resource;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "resource_type", schema = "resource")
@Data
public class ResourceType {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code; // ex: CORPORATE_PHONE, MONITOR

    @Column(nullable = false)
    private String name; // ex: Telefone Corporativo

    @Column
    private String description;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
