package com.ecoprem.auth.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class BackupCodeResponse {
    private UUID id;
    private String code;
    private boolean used;
}
