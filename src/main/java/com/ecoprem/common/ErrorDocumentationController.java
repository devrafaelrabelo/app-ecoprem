package com.ecoprem.common;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/docs/errors")
public class ErrorDocumentationController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> listAllErrors() {
        return List.of(
                Map.of("type", ErrorType.INVALID_CREDENTIALS.getUri(), "title", ErrorType.INVALID_CREDENTIALS.getTitle(), "status", 401),
                Map.of("type", ErrorType.REFRESH_TOKEN_EXPIRED.getUri(), "title", ErrorType.REFRESH_TOKEN_EXPIRED.getTitle(), "status", 401),
                Map.of("type", ErrorType.ACCESS_DENIED.getUri(), "title", ErrorType.ACCESS_DENIED.getTitle(), "status", 403),
                Map.of("type", ErrorType.ACCOUNT_LOCKED.getUri(), "title", ErrorType.ACCOUNT_LOCKED.getTitle(), "status", 403),
                Map.of("type", ErrorType.ACCOUNT_SUSPENDED.getUri(), "title", ErrorType.ACCOUNT_SUSPENDED.getTitle(), "status", 403),
                Map.of("type", ErrorType.ACCOUNT_NOT_ACTIVE.getUri(), "title", ErrorType.ACCOUNT_NOT_ACTIVE.getTitle(), "status", 403),
                Map.of("type", ErrorType.INVALID_ROLE_ASSIGNMENT.getUri(), "title", ErrorType.INVALID_ROLE_ASSIGNMENT.getTitle(), "status", 403),
                Map.of("type", ErrorType.TWO_FACTOR_REQUIRED.getUri(), "title", ErrorType.TWO_FACTOR_REQUIRED.getTitle(), "status", 403),
                Map.of("type", ErrorType.RATE_LIMIT_EXCEEDED.getUri(), "title", ErrorType.RATE_LIMIT_EXCEEDED.getTitle(), "status", 429),
                Map.of("type", ErrorType.USERNAME_ALREADY_EXISTS.getUri(), "title", ErrorType.USERNAME_ALREADY_EXISTS.getTitle(), "status", 409),
                Map.of("type", ErrorType.EMAIL_ALREADY_EXISTS.getUri(), "title", ErrorType.EMAIL_ALREADY_EXISTS.getTitle(), "status", 409),
                Map.of("type", ErrorType.USER_NOT_FOUND.getUri(), "title", ErrorType.USER_NOT_FOUND.getTitle(), "status", 404),
                Map.of("type", ErrorType.ROLE_NOT_FOUND.getUri(), "title", ErrorType.ROLE_NOT_FOUND.getTitle(), "status", 404),
                Map.of("type", ErrorType.WEAK_PASSWORD.getUri(), "title", ErrorType.WEAK_PASSWORD.getTitle(), "status", 400),
                Map.of("type", ErrorType.INVALID_2FA_TOKEN.getUri(), "title", ErrorType.INVALID_2FA_TOKEN.getTitle(), "status", 400),
                Map.of("type", ErrorType.EXPIRED_2FA_TOKEN.getUri(), "title", ErrorType.EXPIRED_2FA_TOKEN.getTitle(), "status", 400),
                Map.of("type", ErrorType.INVALID_2FA_CODE.getUri(), "title", ErrorType.INVALID_2FA_CODE.getTitle(), "status", 400),
                Map.of("type", ErrorType.RESOURCE_NOT_FOUND.getUri(), "title", ErrorType.RESOURCE_NOT_FOUND.getTitle(), "status", 404),
                Map.of("type", ErrorType.VALIDATION.getUri(), "title", ErrorType.VALIDATION.getTitle(), "status", 400),
                Map.of("type", ErrorType.INVALID_BODY.getUri(), "title", ErrorType.INVALID_BODY.getTitle(), "status", 400),
                Map.of("type", ErrorType.INVALID_TOKEN.getUri(), "title", ErrorType.INVALID_TOKEN.getTitle(), "status", 401),
                Map.of("type", ErrorType.INVALID_RESOURCE_TYPE.getUri(),
                        "title", ErrorType.INVALID_RESOURCE_TYPE.getTitle(),
                        "status", 400
                ),
                Map.of(
                        "type", ErrorType.INVALID_RESOURCE_STATUS.getUri(),
                        "title", ErrorType.INVALID_RESOURCE_STATUS.getTitle(),
                        "status", 400
                ),
                Map.of(
                        "type", ErrorType.INVALID_COMPANY.getUri(),
                        "title", ErrorType.INVALID_COMPANY.getTitle(),
                        "status", 400
                ),
                Map.of(
                        "type", ErrorType.INVALID_RESOURCE_TYPE.getUri(),
                        "title", ErrorType.INVALID_RESOURCE_TYPE.getTitle(),
                        "status", 400
                ),
                Map.of(
                        "type", ErrorType.INVALID_USER.getUri(),
                        "title", ErrorType.INVALID_USER.getTitle(),
                        "status", 400
                )
        );
    }
}
