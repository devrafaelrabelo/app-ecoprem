package com.controlcenter.user.dto;

import com.controlcenter.entity.user.User;
import lombok.Data;

import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    private String username;
    private String email;
    private String fullName;

    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        return dto;
    }
}
