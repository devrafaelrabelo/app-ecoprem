package com.ecoprem.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileDTO {
    private String userId;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private boolean twoFactorEnabled;
}