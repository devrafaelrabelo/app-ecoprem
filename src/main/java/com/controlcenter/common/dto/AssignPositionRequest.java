package com.controlcenter.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AssignPositionRequest {

    @NotNull
    @Schema(description = "ID do usuário", example = "8d9e8d9f-92ab-a5b7-ff6c-889900112233")
    private UUID userId;

    @NotNull
    @Schema(description = "ID do cargo (position) a ser atribuído", example = "88a9b123-bc4f-4e55-8fa3-778899001199")
    private UUID positionId;
}