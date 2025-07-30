package com.controlcenter.admin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminPermissionDTO {
    private UUID id;
    private String name;
    private String description;

}

