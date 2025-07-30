package com.controlcenter.entity.auth;

import com.controlcenter.entity.user.User;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "login_history", schema = "auth")
@Data
public class LoginHistory {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "login_date", nullable = false)
    private LocalDateTime loginDate;

    @Column(name = "ip_address")
    private String ipAddress;

    private String location;

    private String device;

    private String browser;

    @Column(name = "operating_system")
    private String operatingSystem;

    private boolean success;

    private String failureReason; // opcional, só preenchido se success == false

}
