package com.ecoprem.admin.dto;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class AdminRoleCreateUpdateDTO {
    private String name;
    private String description;
    private boolean systemRole;
    private Set<UUID> permissionIds;
}