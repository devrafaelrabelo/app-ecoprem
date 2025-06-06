package com.ecoprem.entity.auth;

import com.ecoprem.entity.security.AccessLevel;
import com.ecoprem.entity.common.Department;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "social_name")
    private String socialName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(nullable = false)
    private String password;

    @Column(name = "password_last_updated")
    private LocalDateTime passwordLastUpdated;

    @Column(name = "account_locked")
    private boolean accountLocked;

    @Column(name = "account_deletion_requested")
    private boolean accountDeletionRequested;

    @Column(name = "account_deletion_request_date")
    private LocalDateTime accountDeletionRequestDate;

    private String origin;

    @Column(name = "interface_theme")
    private String interfaceTheme;

    private String timezone;

    @Column(name = "notifications_enabled")
    private boolean notificationsEnabled;

    @Column(name = "login_attempts")
    private int loginAttempts;

    @Column(name = "last_password_change_ip")
    private String lastPasswordChangeIp;

    @Column(name = "terms_accepted_at")
    private LocalDateTime termsAcceptedAt;

    @Column(name = "privacy_policy_version")
    private String privacyPolicyVersion;

    private String avatar;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "first_login")
    private boolean firstLogin;

    @Column(name = "preferred_language")
    private String preferredLanguage;

    @Column(name = "invitation_status")
    private String invitationStatus;

    @Column(name = "account_suspended_reason")
    private String accountSuspendedReason;

    @Column(name = "last_known_location")
    private String lastKnownLocation;

    @Column(name = "password_compromised")
    private boolean passwordCompromised;

    @Column(name = "forced_logout_at")
    private LocalDateTime forcedLogoutAt;

    @Column(name = "cookie_consent_status")
    private String cookieConsentStatus;

    @Column(name = "manager_id")
    private UUID managerId;

    @Column(name = "two_factor_secret")
    private String twoFactorSecret;

    @Column(name = "two_factor_enabled")
    private boolean twoFactorEnabled;

    @Column(name = "account_locked_at")
    private LocalDateTime accountLockedAt;

    // RELACIONAMENTOS
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "access_level_id")
    private AccessLevel accessLevel;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private UserStatus userStatus;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private UserGroup userGroup;
}
