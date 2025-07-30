package com.controlcenter.resource.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ResourceResponse {

    private UUID id;
    private String name;
    private String assetTag;
    private String serialNumber;
    private String brand;
    private String model;
    private String status;
    private String type;
    private String company;
    private String currentUser;
    private String location;
}