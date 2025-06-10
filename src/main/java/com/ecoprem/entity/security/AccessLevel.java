package com.ecoprem.entity.security;

import com.ecoprem.entity.auth.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
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

    @OneToMany(mappedBy = "accessLevel", fetch = FetchType.LAZY)
    private List<User> users;
}
