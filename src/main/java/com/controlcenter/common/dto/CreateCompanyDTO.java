package com.controlcenter.common.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCompanyDTO {

    @NotBlank
    private String name;

    private String cnpj;

    private String legalName;

    @Valid
    private AddressDTO address;

    private boolean active = true;
}