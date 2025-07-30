package com.controlcenter.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
public class EditUserDTO {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String fullName;
    private String socialName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String cpf;

    @NotNull
    private LocalDate birthDate;

    private UUID positionId;
    private UUID statusId;

    private Set<UUID> roleIds;
    private Set<UUID> departmentIds;
    private Set<UUID> functionIds;
    private Set<UUID> corporatePhoneIds;
    private Set<UUID> internalExtensionIds;

    private String preferredLanguage;
    private String interfaceTheme;
    private boolean notificationsEnabled;
}
