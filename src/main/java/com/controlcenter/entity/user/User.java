package com.controlcenter.entity.user;

import com.controlcenter.entity.common.Function;
import com.controlcenter.entity.common.Position;
import com.controlcenter.entity.common.Address;
import com.controlcenter.entity.resource.AllocationHistory;
import com.controlcenter.entity.common.Department;
import com.controlcenter.entity.common.SubTeam;
import com.controlcenter.entity.communication.CorporatePhone;
import com.controlcenter.entity.communication.InternalExtension;
import com.controlcenter.entity.security.Role;
import com.controlcenter.entity.security.UserPermission;
import com.controlcenter.entity.security.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Table(name = "users", schema = "security")
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @Builder.Default
    @Column(name = "email_verified")
    private boolean emailVerified = false;

    @Column(nullable = false)
    private String password;

    @Column(name = "password_last_updated")
    private LocalDateTime passwordLastUpdated;

    @Builder.Default
    @Column(name = "account_locked")
    private boolean accountLocked = false;

    @Column(name = "account_locked_at")
    private LocalDateTime accountLockedAt;

    @Builder.Default
    @Column(name = "account_deletion_requested")
    private boolean accountDeletionRequested = false;

    @Column(name = "account_deletion_request_date")
    private LocalDateTime accountDeletionRequestDate;

    private String origin;

    @Builder.Default
    @Column(name = "interface_theme")
    private String interfaceTheme = "light";

    @Builder.Default
    private String timezone = "America/Sao_Paulo";

    @Builder.Default
    @Column(name = "notifications_enabled")
    private boolean notificationsEnabled = true;

    @Builder.Default
    @Column(name = "login_attempts")
    private int loginAttempts = 0;

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

    @Builder.Default
    @Column(name = "first_login")
    private boolean firstLogin = true;

    @Builder.Default
    @Column(name = "preferred_language")
    private String preferredLanguage = "pt-BR";

    @Column(name = "invitation_status")
    private String invitationStatus;

    @Column(name = "account_suspended_reason")
    private String accountSuspendedReason;

    @Column(name = "last_known_location")
    private String lastKnownLocation;

    @Builder.Default
    @Column(name = "password_compromised")
    private boolean passwordCompromised = false;

    @Column(name = "forced_logout_at")
    private LocalDateTime forcedLogoutAt;

    @Column(name = "cookie_consent_status")
    private String cookieConsentStatus;

    @Column(name = "manager_id")
    private UUID managerId;

    @Column(name = "two_factor_secret")
    private String twoFactorSecret;

    @Builder.Default
    @Column(name = "two_factor_enabled")
    private boolean twoFactorEnabled = false;

    // RELACIONAMENTOS

    @ManyToOne
    @JoinColumn(name = "requested_by_id")
    private User requestedBy;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @Builder.Default
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_role",
            schema = "security",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private UserStatus status;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "user_department",
            schema = "user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private Set<Department> departments = new HashSet<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "user_user_group",
            schema = "security",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "user_group_id")
    )
    private Set<UserGroup> userGroups = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "user_function",
            schema = "common",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "function_id")
    )
    private Set<Function> functions = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserPermission> userPermissions = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_personal_phones", schema = "user", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "phone_number")
    @Builder.Default
    private Set<String> personalPhoneNumbers = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AllocationHistory> allocationHistories = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "currentUser")
    private Set<InternalExtension> currentInternalExtensions = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "currentUser")
    private Set<CorporatePhone> currentCorporatePhones = new HashSet<>();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "address_street")),
            @AttributeOverride(name = "number", column = @Column(name = "address_number")),
            @AttributeOverride(name = "complement", column = @Column(name = "address_complement")),
            @AttributeOverride(name = "city", column = @Column(name = "address_city")),
            @AttributeOverride(name = "neighborhood", column = @Column(name = "address_neighborhood")),
            @AttributeOverride(name = "state", column = @Column(name = "address_state")),
            @AttributeOverride(name = "country", column = @Column(name = "address_country")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "address_postal_code"))
    })
    private Address address;

    @ManyToMany(mappedBy = "users")
    private List<SubTeam> subTeams;

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
