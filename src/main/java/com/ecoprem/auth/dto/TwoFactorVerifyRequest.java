package com.ecoprem.auth.dto;

import lombok.Data;

@Data
public class TwoFactorVerifyRequest {
    private String code;
}
