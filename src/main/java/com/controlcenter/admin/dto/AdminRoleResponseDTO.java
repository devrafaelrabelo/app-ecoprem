package com.controlcenter.admin.dto;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class AdminRoleResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private boolean systemRole;
    private Set<PermissionInfo> permissions;
    private int permissionCount; // novo campo

    @Data
    public static class PermissionInfo {
        private UUID id;
        private String name;
        private String description;
    }
}

