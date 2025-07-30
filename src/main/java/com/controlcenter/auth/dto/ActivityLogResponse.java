package com.controlcenter.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "Resumo de uma atividade recente do usuário.")
public class ActivityLogResponse {

    @Schema(description = "Identificador único do log de atividade")
    private UUID id;

    @Schema(description = "Descrição da ação realizada")
    private String activity;

    @Schema(description = "Data e hora em que a atividade ocorreu")
    private LocalDateTime activityDate;

    @Schema(description = "Endereço IP de onde a ação foi executada")
    private String ipAddress;

    @Schema(description = "Localização geográfica aproximada (baseado no IP)")
    private String location;
}
