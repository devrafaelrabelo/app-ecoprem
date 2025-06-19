package com.ecoprem.admin.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AdminPermissionDTO {
    private UUID id;
    private String name;
    private String description;
}

