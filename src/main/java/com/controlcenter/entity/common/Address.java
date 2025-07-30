package com.controlcenter.entity.common;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Address {

    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private String country;
    private String postalCode;
}
