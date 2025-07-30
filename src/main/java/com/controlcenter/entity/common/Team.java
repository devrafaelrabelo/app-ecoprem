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
@Table(name = "team", schema = "common")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String location;

    @ManyToOne
    @JoinColumn(name = "supervisor_id")
    private User supervisor;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private List<SubTeam> subTeams = new ArrayList<>();
}