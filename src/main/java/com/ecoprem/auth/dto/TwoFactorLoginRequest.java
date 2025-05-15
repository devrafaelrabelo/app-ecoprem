package com.ecoprem.auth.dto;

import lombok.Data;

@Data
public class TwoFactorLoginRequest {
    private String twoFactorCode;
    private String tempToken;
    private boolean rememberMe = false;
}
