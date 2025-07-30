package com.controlcenter.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserDetailsDTO {
    private UUID id;
    private String username;
    private String fullName;
    private String socialName;
    private String email;
    private String cpf;
    private LocalDate birthDate;
    private List<String> personalPhoneNumbers;
    private List<String> currentCorporatePhones;
    private List<String> currentInternalExtensions;

    private List<String> roles;
    private List<String> departments;
    private List<String> permissions;
    private String position;
    private List<String> functions;

    private String status;
    private boolean locked;
    private boolean twoFactorEnabled;
    private boolean emailVerified;
    private boolean firstLogin;
    private boolean passwordCompromised;

    private String preferredLanguage;
    private String interfaceTheme;
    private String timezone;
    private boolean notificationsEnabled;
    private String avatar;

    private String invitationStatus;
    private String origin;
    private String lastKnownLocation;
    private String accountSuspendedReason;
    private String cookieConsentStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime forcedLogoutAt;
    private LocalDateTime passwordLastUpdated;
}
