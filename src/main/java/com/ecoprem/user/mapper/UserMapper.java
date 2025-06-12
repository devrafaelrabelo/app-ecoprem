package com.ecoprem.user.mapper;

import com.ecoprem.auth.dto.UserBasicDTO;
import com.ecoprem.entity.auth.Function;
import com.ecoprem.entity.user.User;
import com.ecoprem.entity.user.UserGroup;
import com.ecoprem.entity.common.Department;
import com.ecoprem.entity.security.Role;

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
}