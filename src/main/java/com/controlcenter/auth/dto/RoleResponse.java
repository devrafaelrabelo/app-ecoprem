package com.controlcenter.auth.dto;

import java.util.UUID;

public record RoleResponse(
        UUID id,
        String name,
        String description,
        boolean systemRole
) {}
