package com.ecoprem.user.mapper;

import com.ecoprem.entity.communication.CorporatePhone;
import com.ecoprem.entity.communication.InternalExtension;
import com.ecoprem.user.dto.UserBasicDTO;
import com.ecoprem.entity.auth.Function;
import com.ecoprem.entity.common.AllocationHistory;
import com.ecoprem.entity.common.Company;
import com.ecoprem.entity.user.User;
import com.ecoprem.entity.user.UserGroup;
import com.ecoprem.entity.common.Department;
import com.ecoprem.entity.security.Role;
import com.ecoprem.user.dto.ProfileDTO;

import java.util.ArrayList;
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
                .personalPhoneNumbers(user.getPersonalPhoneNumbers())
                .currentCorporatePhones(new ArrayList<>(user.getCurrentCorporatePhones()))
                .currentInternalExtensions(new ArrayList<>(user.getCurrentInternalExtensions()))
                .companyId(company != null ? company.getId() : null)
                .companyName(company != null ? company.getName() : null)
                .companyCnpj(company != null ? company.getCnpj() : null)
                .companyLegalName(company != null ? company.getLegalName() : null)
                .companyAddress(company != null ? company.getAddress() : null)
                .build();
    }



}