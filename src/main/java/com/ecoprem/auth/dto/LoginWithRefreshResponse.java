package com.ecoprem.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginWithRefreshResponse {
    private String accessToken;
    private String refreshToken;
    private String username;
    private String fullName;
    private boolean twoFactorEnabled;
}
