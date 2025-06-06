package com.ecoprem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "webhook")
@Data
public class Webhook {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String url;
}
