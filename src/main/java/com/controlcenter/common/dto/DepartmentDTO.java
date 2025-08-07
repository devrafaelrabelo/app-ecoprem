package com.controlcenter.common.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class DepartmentDTO {
    private UUID id;
    private String name;
    private String description;
    private UUID managerId;
}