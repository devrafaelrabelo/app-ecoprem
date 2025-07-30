package com.controlcenter.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceRequest {
    private String name;
    private String brand;
    private String model;
    private String assetTag;
    private String serialNumber;
    private String responsibleSector;
    private BigDecimal price;
    private LocalDate purchaseDate;
    private LocalDate warrantyEndDate;
    private boolean availableForUse;
    private String description;
    private String location;
    private UUID currentUserId;

    private UUID resourceTypeId;
    private UUID statusId;
    private UUID companyId;
}
