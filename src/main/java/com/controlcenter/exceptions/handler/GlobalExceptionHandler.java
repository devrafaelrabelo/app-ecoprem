package com.controlcenter.exceptions.handler;

import com.controlcenter.exceptions.dto.ApiError;
import com.controlcenter.exceptions.enums.ErrorType;
import com.controlcenter.exceptions.exception.*;
import com.sun.jdi.request.InvalidRequestStateException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiError> buildError(HttpStatus status, ErrorType errorType, String detail, String instance, Map<String, Object> details) {
        return new ResponseEntity<>(
                new ApiError(
                        errorType.getUri(),
                        errorType.getTitle(),
                        status.value(),
                        detail,
                        instance,
                        LocalDateTime.now(),
                        details
                ),
                status
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, Object> validationErrors = new HashMap<>();
        validationErrors.put("fields", ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> Map.of(
                        "field", fieldError.getField(),
                        "message", fieldError.getDefaultMessage()
                )).collect(Collectors.toList()));

        return buildError(
                HttpStatus.BAD_REQUEST,
                ErrorType.VALIDATION,
                "One or more fields are invalid.",
                request.getRequestURI(),
                validationErrors
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                ErrorType.INVALID_BODY,
                "Malformed or missing JSON payload.",
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiError> handleAccountLocked(AccountLockedException ex, HttpServletRequest request) {
        return buildError(HttpStatus.FORBIDDEN, ErrorType.ACCOUNT_LOCKED, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException ex, HttpServletRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, ErrorType.INVALID_CREDENTIALS, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(TwoFactorRequiredException.class)
    public ResponseEntity<ApiError> handleTwoFactorRequired(TwoFactorRequiredException ex, HttpServletRequest request) {
        Map<String, Object> details = Map.of("tempToken", ex.getTempToken());
        return buildError(HttpStatus.FORBIDDEN, ErrorType.TWO_FACTOR_REQUIRED, ex.getMessage(), request.getRequestURI(), details);
    }

    @ExceptionHandler(AccountSuspendedException.class)
    public ResponseEntity<ApiError> handleAccountSuspended(AccountSuspendedException ex, HttpServletRequest request) {
        return buildError(HttpStatus.FORBIDDEN, ErrorType.ACCOUNT_SUSPENDED, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyExists(EmailAlreadyExistsException ex, HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, ErrorType.EMAIL_ALREADY_EXISTS, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex, HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, ErrorType.USERNAME_ALREADY_EXISTS, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ApiError> handleRoleNotFound(RoleNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ErrorType.ROLE_NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(PasswordTooWeakException.class)
    public ResponseEntity<ApiError> handlePasswordTooWeak(PasswordTooWeakException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ErrorType.WEAK_PASSWORD, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(InvalidRoleAssignmentException.class)
    public ResponseEntity<ApiError> handleInvalidRoleAssignment(InvalidRoleAssignmentException ex, HttpServletRequest request) {
        return buildError(HttpStatus.FORBIDDEN, ErrorType.INVALID_ROLE_ASSIGNMENT, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(AccountNotActiveException.class)
    public ResponseEntity<ApiError> handleAccountNotActive(AccountNotActiveException ex, HttpServletRequest request) {
        return buildError(HttpStatus.FORBIDDEN, ErrorType.ACCOUNT_NOT_ACTIVE, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiError> handleRateLimitExceeded(RateLimitExceededException ex, HttpServletRequest request) {
        return buildError(HttpStatus.TOO_MANY_REQUESTS, ErrorType.RATE_LIMIT_EXCEEDED, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(Invalid2FATokenException.class)
    public ResponseEntity<ApiError> handleInvalid2FAToken(Invalid2FATokenException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ErrorType.INVALID_2FA_TOKEN, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(Expired2FATokenException.class)
    public ResponseEntity<ApiError> handleExpired2FAToken(Expired2FATokenException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ErrorType.EXPIRED_2FA_TOKEN, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(Invalid2FACodeException.class)
    public ResponseEntity<ApiError> handleInvalid2FACode(Invalid2FACodeException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ErrorType.INVALID_2FA_CODE, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<ApiError> handleRefreshTokenExpired(RefreshTokenExpiredException ex, HttpServletRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, ErrorType.REFRESH_TOKEN_EXPIRED, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(CustomAccessDeniedException ex, HttpServletRequest request) {
        return buildError(HttpStatus.FORBIDDEN, ErrorType.ACCESS_DENIED, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiError> handleInvalidToken(InvalidTokenException ex, HttpServletRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, ErrorType.INVALID_TOKEN, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildError(
                HttpStatus.NOT_FOUND,
                ErrorType.RESOURCE_NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(InvalidResourceStatusException.class)
    public ResponseEntity<ApiError> handleInvalidStatus(InvalidResourceStatusException ex, HttpServletRequest request) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                ErrorType.INVALID_RESOURCE_STATUS,
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(InvalidCompanyException.class)
    public ResponseEntity<ApiError> handleInvalidCompany(InvalidCompanyException ex, HttpServletRequest request) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                ErrorType.INVALID_COMPANY,
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(InvalidResourceTypeException.class)
    public ResponseEntity<ApiError> handleInvalidType(InvalidResourceTypeException ex, HttpServletRequest request) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                ErrorType.INVALID_RESOURCE_TYPE,
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<ApiError> handleInvalidUser(InvalidUserException ex, HttpServletRequest request) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                ErrorType.INVALID_USER,
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(InvalidCarrierException.class)
    public ResponseEntity<ApiError> handleCarrier(InvalidCarrierException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ErrorType.INVALID_CARRIER, ex.getMessage(), request.getRequestURI(),null);
    }

    @ExceptionHandler(InvalidPlanTypeException.class)
    public ResponseEntity<ApiError> handlePlanType(InvalidPlanTypeException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ErrorType.INVALID_PLAN_TYPE, ex.getMessage(), request.getRequestURI(),null);
    }

    @ExceptionHandler(InvalidPhoneStatusException.class)
    public ResponseEntity<ApiError> handleStatus(InvalidPhoneStatusException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ErrorType.INVALID_PHONE_STATUS, ex.getMessage(), request.getRequestURI(),null);
    }

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ApiError> handleCompanyNotFound(CompanyNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ErrorType.COMPANY_NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(CorporatePhoneNotFoundException.class)
    public ResponseEntity<ApiError> handleCorporatePhoneNotFound(CorporatePhoneNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ErrorType.CORPORATE_PHONE_NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(DuplicateNumberPhoneException.class)
    public ResponseEntity<ApiError> handleDuplicateNumberPhone(DuplicateNumberPhoneException ex, HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, ErrorType.DUPLICATE_NUMBER_PHONE, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(DuplicateResourceCodeException.class)
    public ResponseEntity<ApiError> handleDuplicateResourceCode(DuplicateResourceCodeException ex, HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, ErrorType.DUPLICATE_RESOURCE_CODE, ex.getMessage(), request.getRequestURI(), null);
    }
    @ExceptionHandler(InvalidPhoneException.class)
    public ResponseEntity<ApiError> handleInvalidPhone(InvalidPhoneException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ErrorType.INVALID_PHONE, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(InternalExtensionException.class)
    public ResponseEntity<ApiError> handleInternalExtensionException(InternalExtensionException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ErrorType.INTERNAL_EXTENSION_ERROR, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflictException(ConflictException ex, HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, ErrorType.CONFLICT, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(PermissionNotFoundException.class)
    public ResponseEntity<ApiError> handlePermissionNotFoundException(PermissionNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ErrorType.PERMISSION_NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ErrorType.INVALID_PARAMETERS, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(UnsupportedQueryParamException.class)
    public ResponseEntity<ApiError> handleUnsupportedQueryParam(UnsupportedQueryParamException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ErrorType.UNSUPPORTED_QUERY_PARAMETER, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<ApiError> handleInvalidDateRange(InvalidDateRangeException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ErrorType.DATE_RANGE_INVALID, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class, ConversionFailedException.class })
    public ResponseEntity<ApiError> handleInvalidDateFormat(Exception ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ErrorType.INVALID_PARAMETERS, ex.getMessage(),request.getRequestURI(),null);
    }

    @ExceptionHandler(ActiveSessionNotFoundException.class)
    public ResponseEntity<ApiError> handleActiveSessionNotFound(ActiveSessionNotFoundException ex, HttpServletRequest request) {
        return buildError(
                HttpStatus.NOT_FOUND,
                ErrorType.ACTIVE_SESSION_NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(UserRequestNotFoundException.class)
    public ResponseEntity<ApiError> handleUserRequestNotFound(UserRequestNotFoundException ex, HttpServletRequest request) {
        return buildError(
                HttpStatus.NOT_FOUND,
                ErrorType.USERREQUEST_NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(UserRequestAlreadyProcessedException.class)
    public ResponseEntity<ApiError> handleUserRequestAlreadyProcessed(UserRequestAlreadyProcessedException ex, HttpServletRequest request) {
        return buildError(
                HttpStatus.CONFLICT,
                ErrorType.OPERATION_NOT_ALLOWED,
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ApiError> handleForbiddenOperation(Exception ex, HttpServletRequest request) {
        return buildError(HttpStatus.FORBIDDEN, ErrorType.FORBIDDEN_OPERATION, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(InvalidRequestStateException.class)
    public ResponseEntity<ApiError> handleInvalidState(Exception ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ErrorType.INVALID_STATE, ex.getMessage(), request.getRequestURI(), null);
    }
}
