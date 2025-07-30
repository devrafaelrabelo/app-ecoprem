package com.controlcenter.admin.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AssignPermissionRequest {
    private UUID userId;
    private UUID permissionId;
}