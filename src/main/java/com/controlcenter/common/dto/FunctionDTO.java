package com.controlcenter.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunctionDTO {
    private UUID id;
    private String name;
    private String description;
    private UUID departmentId;
    private String departmentName;
}
