package com.controlcenter.entity.common;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "company", schema = "common")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(unique = true)
    private String cnpj;

    private String legalName;

    private boolean active;

    @Embedded
    private Address address;
}