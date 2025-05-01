package com.ecoprem.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String fullName; // Opcional: caso seja enviado pronto
    private String socialName;
    private String username;
    private String email;
    private String password;
    private String role; // Ex: USER, ADMIN
}
