package com.controlcenter.entity.auth;

import com.controlcenter.entity.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "revoked_token", schema = "auth")
@Data
public class RevokedToken {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked_at", nullable = false)
    private LocalDateTime revokedAt;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "reason")
    private String reason;

    @Column(name = "revoked_by")
    private String revokedBy;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
