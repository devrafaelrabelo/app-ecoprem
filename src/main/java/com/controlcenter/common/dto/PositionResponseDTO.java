package com.controlcenter.common.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PositionResponseDTO {
    private UUID id;
    private String name;
    private String description;
}