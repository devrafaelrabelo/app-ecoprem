package com.controlcenter.entity.auth;

import com.controlcenter.entity.user.User;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "active_session", schema = "auth")
@Data
public class ActiveSession {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    private String device;

    private String browser;

    @Column(name = "operating_system")
    private String operatingSystem;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "last_access_at")
    private LocalDateTime lastAccessAt;
}
