package com.controlcenter.admin.dto;

import java.util.List;
import java.util.UUID;

public record BatchPermissionAssignmentRequest(
        UUID userId,
        List<UUID> permissionIds
) {}