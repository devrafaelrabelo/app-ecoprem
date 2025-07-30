package com.controlcenter.auth.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class PrivacySettingsResponse {
    private UUID id;
    private boolean shareActivity;
    private boolean showOnlineStatus;
}
