package com.controlcenter.exceptions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard error response (RFC 9457 / Problem Details)")
public class ApiError {

    @Schema(description = "URI identifier for the error type", example = "https://api.controlcenter.com/errors/invalid-credentials")
    private String type;

    @Schema(description = "Short, human-readable summary", example = "Invalid credentials")
    private String title;

    @Schema(description = "HTTP status code", example = "401")
    private int status;

    @Schema(description = "Detailed explanation of the error", example = "The email or password you entered is incorrect.")
    private String detail;

    @Schema(description = "URI of the request where the error occurred", example = "/api/auth/login")
    private String instance;

    @Schema(description = "Timestamp of the error", example = "2025-05-13T20:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "Additional error details", example = "{\"tempToken\": \"abc123\"}")
    private Map<String, Object> details;
}
