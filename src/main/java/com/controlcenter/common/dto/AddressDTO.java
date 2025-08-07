package com.controlcenter.common.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class AddressDTO {

    @Schema(example = "Rua das Flores")
    private String street;

    @Schema(example = "123")
    private String number;

    @Schema(example = "Apto 45")
    private String complement;

    @Schema(example = "São Paulo")
    private String city;

    @Schema(example = "Centro")
    private String neighborhood;

    @Schema(example = "SP")
    private String state;

    @Schema(example = "Brasil")
    private String country;

    @Schema(example = "01000-000")
    private String postalCode;
}