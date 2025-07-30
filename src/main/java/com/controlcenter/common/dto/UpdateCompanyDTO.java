package com.controlcenter.common.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCompanyDTO {

    @NotBlank
    private String name;

    private String cnpj;

    private String legalName;

    @Valid
    private AddressDTO address;

    private boolean active;
}
