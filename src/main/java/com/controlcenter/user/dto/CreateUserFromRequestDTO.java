package com.controlcenter.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserFromRequestDTO {

    @NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotEmpty
    private List<String> roles;

    private UUID requestedById;

    private String positionId;

    private List<String> departmentIds;

    private List<String> groupIds;
}