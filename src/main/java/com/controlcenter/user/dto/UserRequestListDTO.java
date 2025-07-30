package com.controlcenter.user.dto;

import com.controlcenter.enums.UserRequestStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestListDTO {
    private UUID id;
    private String cpf;
    private String firstName;
    private String lastName;
    private String phone;
    private UserRequestStatus status;
    private LocalDateTime requestedAt;
}
