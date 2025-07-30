package com.controlcenter.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "position", schema = "common")
@Data
public class Position {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
}