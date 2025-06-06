package com.ecoprem.entity.auth;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "user_status")
@Data
public class UserStatus {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String status;

    private String description;

    @Column(name = "is_active")
    private boolean active;
}
