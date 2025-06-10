package com.ecoprem.entity.auth;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "user_status")
@Data
public class UserStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 50)
    private String status;

    private String description;

    @Column(name = "is_active")
    private boolean active;
}
