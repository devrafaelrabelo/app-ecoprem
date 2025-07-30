package com.controlcenter.common.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CompanyDTO {
    private UUID id;
    private String name;
    private String cnpj;
    private String legalName;
    private AddressDTO address;  // Agora é um objeto estruturado
    private boolean active;
}