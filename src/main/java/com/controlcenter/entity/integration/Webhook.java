package com.controlcenter.entity.integration;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "webhook", schema = "integration")
@Data
public class Webhook {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String url;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    private String secret;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_called_at")
    private LocalDateTime lastCalledAt;
}
