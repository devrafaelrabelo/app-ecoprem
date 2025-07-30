package com.controlcenter.entity.common;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "function", schema = "common")
@Data
public class Function {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}