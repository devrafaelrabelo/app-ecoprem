package com.ecoprem.admin.dto;

import java.util.UUID;

public record AssignRoleRequest(UUID userId, UUID roleId) {}
