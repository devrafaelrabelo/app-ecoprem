package com.controlcenter.auth.dto;

import lombok.Data;

@Data
public class PrivacySettingsRequest {
    private boolean shareActivity;
    private boolean showOnlineStatus;
}
