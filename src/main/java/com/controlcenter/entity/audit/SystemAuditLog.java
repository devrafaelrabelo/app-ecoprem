package com.controlcenter.entity.audit;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "system_audit_log", schema = "audit")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String action; // ex: "PERMISSION_GRANTED", "USER_DELETED"

    private String targetEntity; // ex: "User", "Permission"
    private String targetId;

    private String performedBy; // username ou email
    private UUID performedById;

    private String ipAddress;
    private String userAgent;
    private String httpMethod;
    private String path;
    private String sessionId;

    @Column(columnDefinition = "TEXT")
    private String details; // JSON serializado com detalhes do evento

    @CreationTimestamp
    private LocalDateTime timestamp;
}

