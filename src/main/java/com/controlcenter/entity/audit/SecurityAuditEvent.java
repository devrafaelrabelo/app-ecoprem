package com.controlcenter.entity.audit;

import com.controlcenter.entity.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "security_audit_event", schema = "audit")
@Data
public class SecurityAuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id_ref", insertable = false, updatable = false)
    private UUID userId;

    @Column(name = "username", length = 150)
    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_security_audit_user"))
    private User user;

    @Column(name = "event_type", length = 255)
    private String eventType;

    @Column(length = 255)
    private String description;

    @Column(name = "ip_address", length = 255)
    private String ipAddress;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(name = "path", length = 255)
    private String path;

    @Column(name = "method", length = 10)
    private String method;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
