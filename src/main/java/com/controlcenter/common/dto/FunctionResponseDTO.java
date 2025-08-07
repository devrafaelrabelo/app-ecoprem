package com.controlcenter.common.dto;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class FunctionResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private UUID departmentId;
    private String departmentName;
}