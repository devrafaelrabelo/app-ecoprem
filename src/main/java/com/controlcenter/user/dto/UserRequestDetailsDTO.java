package com.controlcenter.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDetailsDTO {
    private UUID id;
    private String cpf;
    private String fullName;
    private String status;
    private String birthDate;
    private String phone;
    private String supervisorId;
    private String leaderId;
    private String cep;
    private String street;
    private String neighborhood;
    private String number;
    private String complement;
    private String city;
    private String state;
    private String requestedName;
    private String requestedAt;
    private String createdBy;
    private String createdAt;
}
