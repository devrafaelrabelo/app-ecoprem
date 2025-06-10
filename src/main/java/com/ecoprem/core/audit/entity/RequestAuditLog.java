package com.ecoprem.core.audit.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "request_audit_log")
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
}