package com.ecoprem.auth.dto;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Email;

import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private boolean rememberMe;
}