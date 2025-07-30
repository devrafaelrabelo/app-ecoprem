package com.controlcenter.entity.common;

import com.controlcenter.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sub_team", schema = "common")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;

    @ManyToMany
    @JoinTable(
            name = "user_sub_team",
            schema = "user",
            joinColumns = @JoinColumn(name = "sub_team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users = new ArrayList<>();
}
