package com.controlcenter.auth.util;

import lombok.Data;

@Data
public class GeoIpResponse {
    private String status;
    private String country;
    private String city;
}
