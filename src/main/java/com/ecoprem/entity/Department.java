package com.ecoprem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "department")
@Data
public class Department {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(name = "manager_id")
    private UUID managerId;
}
