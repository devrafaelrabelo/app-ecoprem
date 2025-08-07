package com.controlcenter.common.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class DepartmentManagerAssignDTO {
    private UUID managerId; // pode ser null para remover
}