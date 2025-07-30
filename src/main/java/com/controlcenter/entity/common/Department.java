package com.controlcenter.entity.common;

import com.controlcenter.entity.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "department", schema = "common")
@Data
public class Department {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @OneToOne
    @JoinColumn(name = "manager_id")
    private User manager;

    @ManyToMany(mappedBy = "departments")
    private List<User> users;
}
