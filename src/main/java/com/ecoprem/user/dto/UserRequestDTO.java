package com.ecoprem.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {

    @NotBlank
    private String cpf;

    @NotNull
    private LocalDate birthDate;

    @NotBlank
    private String fullName;

    @NotBlank
    private String phone;

    @NotBlank
    private String supervisorId;

    @NotBlank
    private String leaderId;

    @NotBlank
    private String cep;

    @NotBlank
    private String street;

    @NotBlank
    private String number;

    private String complement;

    @NotBlank
    private String city;

    @NotBlank
    private String state;
}
