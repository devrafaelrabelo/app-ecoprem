package com.controlcenter.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class AdminRoleCreateUpdateDTO {

    @NotBlank(message = "O nome da role é obrigatório")
    @Size(max = 100, message = "O nome da role deve ter no máximo 100 caracteres")
    private String name;

    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    private String description;

    @NotNull(message = "O campo systemRole é obrigatório")
    private boolean systemRole;

    private Set<UUID> permissionIds;
}