package com.ecoprem.core.audit.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "request_audit_log")
@Data
public class RequestAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String method;
    private String path;
    private String ipAddress;
    private int statusCode;

    @Column(length = 1000)
    private String userAgent;

    private LocalDateTime timestamp;
}
