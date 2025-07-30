package com.controlcenter.resource.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class ResourceCreateRequest {

    private String name;
    private String brand;
    private String model;
    private String assetTag;
    private String serialNumber;
    private String responsibleSector;
    private BigDecimal price;
    private LocalDate purchaseDate;
    private LocalDate warrantyEndDate;
    private Boolean availableForUse;
    private String description;
    private String location;

    private UUID currentUserId;
    private UUID resourceTypeId;
    private UUID statusId;
    private UUID companyId;
}