package com.controlcenter.common.dto;

import lombok.Data;

import java.util.UUID;
@Data
public class AssignFunctionRequest {
    private UUID userId;
    private UUID functionId;
}
