package com.controlcenter.common.dto;

import lombok.Data;

@Data
public class AddressDTO {
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private String country;
    private String postalCode;
}