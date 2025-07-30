package com.controlcenter.user.mapper;

import com.controlcenter.entity.communication.CorporatePhone;
import com.controlcenter.entity.communication.InternalExtension;
import com.controlcenter.entity.security.Permission;
import com.controlcenter.entity.security.UserPermission;
import com.controlcenter.user.dto.UserBasicDTO;
import com.controlcenter.entity.common.Function;
import com.controlcenter.entity.resource.AllocationHistory;
import com.controlcenter.entity.common.Company;
import com.controlcenter.entity.user.User;
import com.controlcenter.entity.user.UserGroup;
import com.controlcenter.entity.common.Department;
import com.controlcenter.entity.security.Role;
import com.controlcenter.user.dto.ProfileDTO;
import com.controlcenter.user.dto.UserDetailsDTO;
import com.controlcenter.user.dto.UserListDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserBasicDTO toBasicDTO(User user) {
        return UserBasicDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .preferredLanguage(user.getPreferredLanguage())
                .interfaceTheme(user.getInterfaceTheme())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .departments(user.getDepartments().stream()
                        .map(Department::getName)
                        .collect(Collectors.toList()))
                .userGroups(user.getUserGroups().stream()
                        .map(UserGroup::getName)
                        .collect(Collectors.toList()))
                .position(user.getPosition() != null ? user.getPosition().getName() : null)
                .functions(user.getFunctions().stream()
                        .map(Function::getName)
                        .collect(Collectors.toList()))
                .permissions(user.getUserPermissions().stream()
                        .map(up -> up.getPermission().getName())
                        .collect(Collectors.toList()))
                .build();
    }

    public static ProfileDTO toProfileDto(User user) {
        AllocationHistory currentAllocation = user.getAllocationHistories().stream()
                .filter(a -> a.getEndDate() == null)
                .max(Comparator.comparing(AllocationHistory::getStartDate))
                .orElse(null);

        Company company = currentAllocation != null ? currentAllocation.getCompany() : null;

        return ProfileDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .socialName(user.getSocialName())
                .username(user.getUsername())
                .status(user.getStatus() != null ? user.getStatus().getName() : null)
                .email(user.getEmail())
                .cpf(user.getCpf())
                .birthDate(user.getBirthDate())
                .emailVerified(user.isEmailVerified())
                .interfaceTheme(user.getInterfaceTheme())
                .timezone(user.getTimezone())
                .notificationsEnabled(user.isNotificationsEnabled())
                .preferredLanguage(user.getPreferredLanguage())
                .avatar(user.getAvatar())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .departments(user.getDepartments().stream()
                        .map(Department::getName)
                        .collect(Collectors.toList()))
                .functions(user.getFunctions().stream()
                        .map(Function::getName)
                        .collect(Collectors.toList()))
                .position(user.getPosition())
                .personalPhoneNumbers((List<String>) user.getPersonalPhoneNumbers())
                .currentCorporatePhones(new ArrayList<>(user.getCurrentCorporatePhones()))
                .currentInternalExtensions(new ArrayList<>(user.getCurrentInternalExtensions()))
                .companyId(company != null ? company.getId() : null)
                .companyName(company != null ? company.getName() : null)
                .companyCnpj(company != null ? company.getCnpj() : null)
                .companyLegalName(company != null ? company.getLegalName() : null)
//                .companyAddress(company != null ? company.getAddress() : null)
                .build();
    }

    public static UserListDTO toListDTO(User user) {
        return UserListDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .cpf(user.getCpf())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toList()))
                .departments(user.getDepartments().stream()
                        .map(department -> department.getName())
                        .collect(Collectors.toList()))
                .position(user.getPosition() != null ? user.getPosition().getName() : null)
                .status(user.getStatus() != null ? user.getStatus().getName() : null)
                .locked(user.isAccountLocked())
                .twoFactorEnabled(user.isTwoFactorEnabled())
                .emailVerified(user.isEmailVerified())
                .passwordCompromised(user.isPasswordCompromised())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static UserDetailsDTO toDetailsDTO(User user) {
        return UserDetailsDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .socialName(user.getSocialName())
                .username(user.getUsername())
                .status(user.getStatus() != null ? user.getStatus().getName() : null)
                .email(user.getEmail())
                .cpf(user.getCpf())
                .birthDate(user.getBirthDate())

                .emailVerified(user.isEmailVerified())
                .locked(user.isAccountLocked())
                .twoFactorEnabled(user.isTwoFactorEnabled())
                .firstLogin(user.isFirstLogin())
                .passwordCompromised(user.isPasswordCompromised())

                .interfaceTheme(user.getInterfaceTheme())
                .timezone(user.getTimezone())
                .notificationsEnabled(user.isNotificationsEnabled())
                .preferredLanguage(user.getPreferredLanguage())

                .avatar(user.getAvatar())
                .invitationStatus(user.getInvitationStatus())
                .origin(user.getOrigin())
                .permissions(user.getUserPermissions().stream()
                        .map(UserPermission::getPermission)
                        .map(Permission::getName)
                        .collect(Collectors.toList()))
                .roles(user.getRoles().stream()
                        .map(r -> r.getName())
                        .collect(Collectors.toList()))
                .departments(user.getDepartments().stream()
                        .map(d -> d.getName())
                        .collect(Collectors.toList()))
                .functions(user.getFunctions().stream()
                        .map(f -> f.getName())
                        .collect(Collectors.toList()))
                .position(user.getPosition() != null ? user.getPosition().getName() : null)
                .personalPhoneNumbers(new ArrayList<>(user.getPersonalPhoneNumbers()))
                .currentCorporatePhones(
                        user.getCurrentCorporatePhones() != null
                                ? user.getCurrentCorporatePhones().stream()
                                .map(CorporatePhone::getNumber) // converte para String
                                .collect(Collectors.toList())
                                : Collections.emptyList()
                )
                .currentInternalExtensions(
                        user.getCurrentInternalExtensions() != null
                                ? user.getCurrentInternalExtensions().stream()
                                .map(InternalExtension::getExtension) // converte para String
                                .collect(Collectors.toList())
                                : Collections.emptyList()
                )
                .lastKnownLocation(user.getLastKnownLocation())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .forcedLogoutAt(user.getForcedLogoutAt())
                .passwordLastUpdated(user.getPasswordLastUpdated())
//                .companyId(company != null ? company.getId() : null)
//                .companyName(company != null ? company.getName() : null)
//                .companyCnpj(company != null ? company.getCnpj() : null)
//                .companyLegalName(company != null ? company.getLegalName() : null)
//                .companyAddress(company != null ? company.getAddress() : null)
                .build();
    }
}
