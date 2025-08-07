package com.controlcenter.common.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AssignFunctionsRequest {
    private UUID userId;
    private List<UUID> functionIds;
}
