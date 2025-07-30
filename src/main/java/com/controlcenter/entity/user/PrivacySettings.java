package com.controlcenter.entity.user;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "privacy_settings", schema = "user")
@Data
public class PrivacySettings {

    @Id
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "share_activity")
    private boolean shareActivity;

    @Column(name = "show_online_status")
    private boolean showOnlineStatus;
}
