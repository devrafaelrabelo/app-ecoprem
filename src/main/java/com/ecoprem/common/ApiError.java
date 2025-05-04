package com.ecoprem.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard error response")
public class ApiError {
    @Schema(description = "HTTP status code", example = "401")
    private int status;

    @Schema(description = "Error code", example = "InvalidCredentials")
    private String error;

    @Schema(description = "Detailed error message", example = "The email or password you entered is incorrect.")
    private String message;

    @Schema(description = "Timestamp of the error", example = "2025-05-04T12:34:56")
    private LocalDateTime timestamp;

    @Schema(description = "Additional error details, such as the tempToken for 2FA or other relevant extra information. Can be null if no specific details are provided.")
    private Map<String, Object> details;
}
