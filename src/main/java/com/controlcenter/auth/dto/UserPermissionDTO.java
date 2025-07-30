package com.controlcenter.auth.dto;

import java.util.UUID;

public record UserPermissionDTO(UUID userId, UUID permissionId) {}