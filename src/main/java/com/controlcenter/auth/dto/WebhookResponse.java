package com.controlcenter.auth.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class WebhookResponse {
    private UUID id;
    private String eventType;
    private String url;
}
