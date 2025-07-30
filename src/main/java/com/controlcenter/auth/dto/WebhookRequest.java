package com.controlcenter.auth.dto;

import lombok.Data;

@Data
public class WebhookRequest {
    private String eventType;
    private String url;
}
