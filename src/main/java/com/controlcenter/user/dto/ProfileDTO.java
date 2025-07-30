package com.controlcenter.user.dto;

import com.controlcenter.entity.common.Position;
import com.controlcenter.entity.communication.CorporatePhone;
import com.controlcenter.entity.communication.InternalExtension;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ProfileDTO {
    private UUID id;

    private String firstName;
    private String lastName;
    private String fullName;
    private String socialName;
    private String username;
    private String status;
    private String email;
    private String cpf;
    private LocalDate birthDate;

    private boolean emailVerified;
    private String interfaceTheme;
    private String timezone;
    private boolean notificationsEnabled;
    private String preferredLanguage;
    private String avatar;

    private List<String> roles;
    private List<String> departments;
    private List<String> functions;
    private Position position;

    private List<String> personalPhoneNumbers;
    private List<CorporatePhone> currentCorporatePhones;
    private List<InternalExtension> currentInternalExtensions;

    // Empresa atual
    private UUID companyId;
    private String companyName;
    private String companyCnpj;
    private String companyLegalName;
    private String companyAddress;
}