package com.controlcenter.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PositionRequestDTO {

    @NotBlank(message = "Nome do cargo é obrigatório")
    @Schema(description = "Nome do cargo", example = "Analista de Sistemas")
    private String name;

    @Schema(description = "Descrição do cargo", example = "Responsável por desenvolver e manter sistemas")
    private String description;
}