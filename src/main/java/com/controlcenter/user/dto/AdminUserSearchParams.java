package com.controlcenter.user.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AdminUserSearchParams {

    // texto livre
    private String nameOrEmail;
    private String cpf;

    // enums/strings
    private String status;
    private String role;
    private String department;
    private String position;
    private String preferredLanguage;
    private String interfaceTheme;

    // flags booleanas
    private Boolean locked;
    private Boolean emailVerified;
    private Boolean twoFactorEnabled;
    private Boolean firstLogin;
    private Boolean passwordCompromised;

    // datas
    private LocalDate createdFrom;
    private LocalDate createdTo;
    private LocalDate lastLoginFrom;
    private LocalDate lastLoginTo;
}
