package com.ecoprem.entity.communication;

import com.ecoprem.entity.common.Company;
import com.ecoprem.entity.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "internal_extension")
public class InternalExtension {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String extension;

    private String application;

    @ManyToOne(optional = true)
    @JoinColumn(name = "current_user_id")
    private User currentUser;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}