package com.controlcenter.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class DeleteUserRequestsDTO {
    @NotEmpty
    private List<UUID> requestIds;
}
