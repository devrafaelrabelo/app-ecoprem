package com.controlcenter.entity.auth;

import com.controlcenter.entity.user.User;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "device_token", schema = "auth")
@Data
public class DeviceToken {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "device_name")
    private String deviceName;

    @Column(nullable = false)
    private String token;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
