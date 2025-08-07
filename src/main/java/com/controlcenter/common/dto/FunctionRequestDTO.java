package com.controlcenter.common.dto;

import java.util.UUID;

public record FunctionRequestDTO(
        String name,
        String description,
        UUID departmentId
) {}
