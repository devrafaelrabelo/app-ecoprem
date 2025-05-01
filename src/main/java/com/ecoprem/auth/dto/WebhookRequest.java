package com.ecoprem.auth.dto;

import lombok.Data;

@Data
public class WebhookRequest {
    private String eventType;
    private String url;
}
