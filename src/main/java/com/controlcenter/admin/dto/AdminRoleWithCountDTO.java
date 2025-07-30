package com.controlcenter.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AdminRoleWithCountDTO {
    private UUID id;
    private String name;
    private String description;
    private boolean systemRole;
    private long permissionCount; // ou int, se preferir

    public AdminRoleWithCountDTO() {

    }
}