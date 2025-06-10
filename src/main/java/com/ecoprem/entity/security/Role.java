package com.ecoprem.entity.security;

import com.ecoprem.entity.auth.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "role")
@Data
public class Role {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(name = "is_system_role")
    private boolean systemRole;

    @ManyToMany(mappedBy = "roles")
    private List<User> users = new ArrayList<>();
}
