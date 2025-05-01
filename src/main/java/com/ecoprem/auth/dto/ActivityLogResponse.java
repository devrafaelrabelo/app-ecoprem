package com.ecoprem.auth.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ActivityLogResponse {
    private UUID id;
    private String activity;
    private LocalDateTime activityDate;
    private String ipAddress;
    private String location;
}
