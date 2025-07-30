package com.controlcenter.resource.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class ResourceStatusDto {

    private UUID id;

    @NotBlank(message = "Código é obrigatório")
    private String code;

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    private String description;

    private boolean blocksAllocation;
}