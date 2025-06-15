package com.ecoprem.entity.user;

import com.ecoprem.entity.auth.Function;
import com.ecoprem.entity.auth.Position;
import com.ecoprem.entity.common.AllocationHistory;
import com.ecoprem.entity.common.Department;
import com.ecoprem.entity.communication.CorporatePhone;
import com.ecoprem.entity.communication.InternalExtension;
import com.ecoprem.entity.communication.PersonalPhone;
import com.ecoprem.entity.security.Role;
import com.ecoprem.entity.security.UserPermission;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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

    @Column(name = "cpf", length = 14, unique = true, nullable = false)
    private String cpf;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(nullable = false)
    private String password;

    @Column(name = "password_last_updated")
    private LocalDateTime passwordLastUpdated;

    @Column(name = "account_locked")
    private boolean accountLocked;

    @Column(name = "account_locked_at")
    private LocalDateTime accountLockedAt;

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

    // RELACIONAMENTOS

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private UserStatus status;

    @ManyToMany
    @JoinTable(
            name = "user_department",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private Set<Department> departments = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_user_group",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "user_group_id")
    )
    private Set<UserGroup> userGroups = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    @ManyToMany
    @JoinTable(
            name = "user_function",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "function_id")
    )
    private Set<Function> functions = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPermission> userPermissions = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_personal_phones", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "phone_number")
    private List<String> personalPhoneNumbers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AllocationHistory> allocationHistories = new HashSet<>();

    @OneToMany(mappedBy = "currentUser")
    private Set<InternalExtension> currentInternalExtensions = new HashSet<>();;

    @OneToMany(mappedBy = "currentUser")
    private Set<CorporatePhone> currentCorporatePhones = new HashSet<>();;


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
