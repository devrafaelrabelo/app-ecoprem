package com.controlcenter.entity.auth;

import com.controlcenter.entity.user.User;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "activity_log", schema = "auth")
@Data
public class ActivityLog {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String activity;

    @Column(name = "activity_date", nullable = false)
    private LocalDateTime activityDate;

    @Column(name = "ip_address")
    private String ipAddress;

    private String location;

    @ManyToOne
    @JoinColumn(name = "target_user_id")
    private User targetUser;
}

