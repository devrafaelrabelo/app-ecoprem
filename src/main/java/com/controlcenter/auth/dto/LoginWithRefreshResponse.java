package com.controlcenter.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Login response containing the JWT access token and refresh token")
public class LoginWithRefreshResponse {

    @Schema(description = "Access token (JWT)", example = "eyJhbGciOiJIUzI1...")
    private String accessToken;

    @Schema(description = "Refresh token", example = "bd7f1caa-...")
    private String refreshToken;

    @Schema(description = "Username of the logged-in user")
    private String username;

    @Schema(description = "Full name of the logged-in user")
    private String fullName;

    @Schema(description = "Whether 2FA is enabled")
    private boolean twoFactorEnabled;
}
