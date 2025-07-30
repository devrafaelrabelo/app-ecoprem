package com.controlcenter.admin.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class AdminRoleDTO {
    private UUID id;
    private String name;
    private String description;
    private boolean systemRole;

}
