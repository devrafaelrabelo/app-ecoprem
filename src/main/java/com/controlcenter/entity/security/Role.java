package com.controlcenter.entity.security;

import com.controlcenter.entity.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.*;

@Entity
@Table(name = "role", schema = "security")
@Data
public class Role {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    private String description;

    @Column(name = "is_system_role")
    private boolean systemRole;

    @ToString.Exclude
    @ManyToMany(mappedBy = "roles")
    private List<User> users = new ArrayList<>();

    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "role_permission",
            schema = "security",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();
}
