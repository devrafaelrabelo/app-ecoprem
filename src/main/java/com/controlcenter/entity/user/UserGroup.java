package com.controlcenter.entity.user;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_group", schema = "security")
@Data
public class UserGroup {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToMany(mappedBy = "userGroups")
    private List<User> users = new ArrayList<>();
}
