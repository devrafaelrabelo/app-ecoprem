package com.ecoprem.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "access_level")
@Data
public class AccessLevel {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "level_number")
    private int levelNumber;

    private String description;
}
