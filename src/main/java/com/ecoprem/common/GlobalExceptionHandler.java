package com.ecoprem.common;

import com.ecoprem.auth.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Trata erros de validação de campos (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("status", 400);
        errors.put("message", "Validation failed");

        errors.put("errors", ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> {
                    Map<String, String> error = new HashMap<>();
                    error.put("field", fieldError.getField());
                    error.put("message", fieldError.getDefaultMessage());
                    return error;
                })
                .collect(Collectors.toList())
        );

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Trata request body inválido ou ausente (ex: JSON faltando)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", 400);
        error.put("message", "Invalid or missing request body");

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<?> handleAccountLocked(AccountLockedException ex) {
        ApiError error = new ApiError(
                403,
                "AccountLocked",
                ex.getMessage(),
                LocalDateTime.now(),
                null  // Podemos adicionar detalhes como lockTime se quiser
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException ex) {
        ApiError error = new ApiError(
                401,
                "InvalidCredentials",
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException ex) {
        ApiError error = new ApiError(
                404,
                "UserNotFound",
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TwoFactorRequiredException.class)
    public ResponseEntity<?> handleTwoFactorRequired(TwoFactorRequiredException ex) {
        Map<String, Object> details = new HashMap<>();
        details.put("tempToken", ex.getTempToken());

        ApiError error = new ApiError(
                403,
                "TwoFactorRequired",
                ex.getMessage(),
                LocalDateTime.now(),
                details
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccountSuspendedException.class)
    public ResponseEntity<?> handleAccountSuspended(AccountSuspendedException ex) {
        ApiError error = new ApiError(
                403,
                "AccountSuspended",
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<?> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        ApiError error = new ApiError(
                409,
                "EmailAlreadyExists",
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<?> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex) {
        ApiError error = new ApiError(
                409,
                "UsernameAlreadyExists",
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<?> handleRoleNotFound(RoleNotFoundException ex) {
        ApiError error = new ApiError(
                404,
                "RoleNotFound",
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PasswordTooWeakException.class)
    public ResponseEntity<?> handlePasswordTooWeak(PasswordTooWeakException ex) {
        ApiError error = new ApiError(
                400,
                "PasswordTooWeak",
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRoleAssignmentException.class)
    public ResponseEntity<?> handleInvalidRoleAssignment(InvalidRoleAssignmentException ex) {
        ApiError error = new ApiError(
                403,
                "InvalidRoleAssignment",
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccountNotActiveException.class)
    public ResponseEntity<?> handleAccountNotActive(AccountNotActiveException ex) {
        ApiError error = new ApiError(
                403,
                "AccountNotActive",
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<?> handleRateLimitExceeded(RateLimitExceededException ex) {
        ApiError error = new ApiError(
                429,
                "RateLimitExceeded",
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(Invalid2FATokenException.class)
    public ResponseEntity<?> handleInvalid2FAToken(Invalid2FATokenException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Expired2FATokenException.class)
    public ResponseEntity<?> handleExpired2FAToken(Expired2FATokenException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Invalid2FACodeException.class)
    public ResponseEntity<?> handleInvalid2FACode(Invalid2FACodeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<?> handleRefreshTokenExpired(RefreshTokenExpiredException ex) {
        ApiError error = new ApiError(
                401,
                "RefreshTokenExpired",
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        logger.warn("ACCESS DENIED: {} {} from IP {}", request.getMethod(), request.getRequestURI(), request.getRemoteAddr());
        ApiError error = new ApiError(
                403,
                "AccessDenied",
                "You are not authorized to access this resource.",
                LocalDateTime.now(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }


}
