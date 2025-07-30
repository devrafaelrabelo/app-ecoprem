package com.controlcenter.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBasicDTO {
    private UUID id;
    private String fullName;
    private String email;
    private String avatar;
    private String preferredLanguage;
    private String interfaceTheme;
    private List<String> roles;
    private List<String> departments;
    private List<String> userGroups;
    private String position;
    private List<String> functions;
    private List<String> permissions;
}