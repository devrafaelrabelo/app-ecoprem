package com.controlcenter.entity.security;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "permission", schema = "security")
@Data
public class Permission {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name; // Ex: "user:create", "auth:reset-password"

    private String description;

    @ManyToMany(mappedBy = "permissions")
    private List<Role> roles;

    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPermission> userPermissions = new ArrayList<>();
}