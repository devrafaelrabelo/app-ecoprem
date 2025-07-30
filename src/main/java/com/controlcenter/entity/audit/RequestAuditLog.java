package com.controlcenter.entity.audit;

import com.controlcenter.entity.user.User;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "request_audit_log", schema = "audit")
@Data
public class RequestAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10)
    private String method;

    @Column(length = 255)
    private String path;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "status_code")
    private int statusCode;

    @Column(name = "user_agent", length = 1000)
    private String userAgent;

    @Column(length = 150)
    private String username;

    @Column(name = "duration_ms")
    private Integer durationMs;

    private LocalDateTime timestamp;

    // ✅ Relacionamento com o usuário
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_request_log_user"))
    private User user;

    @Column(name = "user_id_ref")
    private UUID userId;
}
