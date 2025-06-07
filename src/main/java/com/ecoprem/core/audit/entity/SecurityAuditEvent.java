package com.ecoprem.core.audit.entity;

import com.ecoprem.entity.auth.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "security_audit_event")
@Data
public class SecurityAuditEvent {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String eventType; // ex: "SUSPICIOUS_SESSION", "TOKEN_REVOKED"

    private String description;

    @Column(name = "ip_address")
    private String ipAddress;

    private String userAgent;

    private LocalDateTime timestamp;
}
