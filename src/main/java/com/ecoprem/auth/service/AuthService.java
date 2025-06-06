package com.ecoprem.auth.service;

import com.ecoprem.auth.config.AuthProperties;
import com.ecoprem.auth.dto.*;
import com.ecoprem.auth.entity.*;
import com.ecoprem.auth.exception.*;
import com.ecoprem.auth.repository.*;
import com.ecoprem.auth.security.JwtTokenProvider;
import com.ecoprem.auth.util.JwtCookieUtil;
import com.ecoprem.auth.util.LoginMetadataExtractor;
import com.github.benmanes.caffeine.cache.Cache;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


import static com.ecoprem.auth.util.ValidationUtil.isStrongPassword;
import static com.ecoprem.auth.util.ValidationUtil.isValidEmail;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginHistoryRepository loginHistoryRepository;
    private final LoginMetadataExtractor metadataExtractor;
    private final ActivityLogService activityLogService;
    private final ActiveSessionService activeSessionService;
    private final Pending2FALoginRepository pending2FALoginRepository;
    private final MailService mailService;
    public static final Set<String> ALLOWED_REGISTRATION_ROLES = Set.of("CLIENT", "BASIC_USER");


    private final Cache<String, Integer> loginAttemptsPerIp;
    private final Cache<String, Integer> loginAttemptsPerEmail;
    private final Cache<String, Integer> refreshAttemptsPerIp;
    private final RevokedTokenService revokedTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtCookieUtil jwtCookieUtil;
    private static final int MAX_ATTEMPTS_PER_MINUTE = 10;
    private static final int MAX_REFRESH_ATTEMPTS_PER_MINUTE = 10;
    private static final int MAX_ATTEMPTS_PER_EMAIL_PER_MINUTE = 10;

    @Autowired
    private AuthProperties authProperties;

    public LoginResult login(LoginRequest request, HttpServletRequest servletRequest) {
        String ipAddress = metadataExtractor.getClientIp(servletRequest);
        String userAgent = metadataExtractor.getUserAgent(servletRequest);

        User user = null;
        boolean success = false;
        String failureReason = null;

        try {
            if (!isValidEmail(request.getEmail())) {
                throw new InvalidRequestException("Invalid email format.");
            }

            checkLoginRateLimits(ipAddress, request.getEmail());

            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
            if (userOpt.isEmpty()) {
                throw new InvalidCredentialsException("The email or password you entered is incorrect.");
            }

            user = userOpt.get();

            try {
                validateUserState(user);
            } catch (RuntimeException e) {
                failureReason = e.getMessage();
                throw e;
            }

            success = passwordEncoder.matches(request.getPassword(), user.getPassword());
            if (!success) {
                failureReason = "Invalid password";
                handleInvalidPassword(user);
            }

            user.setLoginAttempts(0);
            userRepository.save(user);

            if (user.isTwoFactorEnabled()) {
                Pending2FALogin pending = createPending2FALogin(user);
                throw new TwoFactorRequiredException("Two-factor authentication is required.", pending.getTempToken());
            }

            String sessionId = UUID.randomUUID().toString();
            activeSessionService.createSession(user, sessionId, servletRequest);

            String token = jwtTokenProvider.generateToken(
                    user.getId(),
                    user.getEmail(),
                    user.getRole().getName(),
                    sessionId
            );

            activityLogService.logActivity(user, "Logged in successfully", servletRequest);

            return new LoginResult(
                    new LoginWithRefreshResponse(
                            token,
                            null,
                            user.getUsername(),
                            user.getFullName(),
                            user.isTwoFactorEnabled()
                    ),
                    user
            );

        } finally {
            if (user != null) {
                recordLoginAttempt(user, ipAddress, userAgent, success, failureReason);
            }
        }
    }

    public void logout(User user, String token, HttpServletRequest request, HttpServletResponse response) {
        try {
            String sessionId = extractSessionIdFromRequest(request);
            LocalDateTime expiresAt = jwtTokenProvider.getExpirationDateFromJWT(token);

            revokedTokenService.revokeToken(token, user, expiresAt);
            refreshTokenRepository.deleteByUserId(user.getId());
            clearAuthCookies(response);

            activityLogService.logActivity(user, "Logged out successfully", request);
            log.info("User {} logged out (sessionId={})", user.getEmail(), sessionId);

        } catch (InvalidRequestException e) {
            log.warn("Logout falhou - sessão inválida: {}", e.getMessage());
            clearAuthCookies(response);

        } catch (Exception e) {
            log.error("Erro inesperado durante logout: {}", e.getMessage(), e);
            clearAuthCookies(response);
        }
    }

    public LoginWithRefreshResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String ipAddress = metadataExtractor.getClientIp(request);
            checkRefreshRateLimit(ipAddress);

            RefreshToken oldToken = resolveValidRefreshToken(request);
            User user = oldToken.getUser();

            refreshTokenRepository.deleteByUserId(user.getId());

            String sessionId = resolveSessionId(request);
            Duration duration = getRefreshDuration(oldToken);

            RefreshToken newRefresh = createNewRefreshToken(user, duration);
            refreshTokenRepository.save(newRefresh);

            String accessToken = issueTokensAndSetCookies(user, sessionId, newRefresh, duration, response);

            activityLogService.logActivity(user, "Refreshed token via cookie", request);

            return new LoginWithRefreshResponse(
                    accessToken,
                    newRefresh.getToken(),
                    user.getUsername(),
                    user.getFullName(),
                    user.isTwoFactorEnabled()
            );

        } catch (MissingTokenException | RefreshTokenExpiredException | RateLimitExceededException e) {
            clearAuthCookies(response);
            throw e;

        } catch (Exception e) {
            clearAuthCookies(response);
            throw new InvalidTokenException("Erro inesperado ao renovar a sessão.");
        }
    }

    public LoginWithRefreshResponse completeLogin(User user, boolean rememberMe,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) {

        String sessionId = createUserSession(user, request);
        String accessToken = generateAccessToken(user, sessionId);

        Duration duration = getRefreshDurationByRememberMe(rememberMe);
        RefreshToken refreshToken = createAndStoreRefreshToken(user, duration);

        jwtCookieUtil.setTokenCookie(response, accessToken);
        jwtCookieUtil.setRefreshTokenCookie(response, refreshToken.getToken(), duration);

        activityLogService.logActivity(user, "Login realizado com rememberMe=" + rememberMe, request);

        return new LoginWithRefreshResponse(
                accessToken,
                refreshToken.getToken(),
                user.getUsername(),
                user.getFullName(),
                user.isTwoFactorEnabled()
        );
    }

    public Map<String, Object> validateAccessToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = extractAccessTokenOrThrow(request);
            Claims claims = jwtTokenProvider.extractClaims(token);
            return buildClaimsResponse(claims);

        } catch (ExpiredJwtException e) {
            jwtCookieUtil.clearTokenCookie(response);
            throw new InvalidTokenException("Token expirado.");

        } catch (JwtException | IllegalArgumentException e) {
            jwtCookieUtil.clearTokenCookie(response);
            throw new InvalidTokenException("Token inválido.");
        }
    }

    public void register(RegisterRequest request) {
        validateEmailAndUsernameUniqueness(request);
        validatePasswordStrength(request.getPassword());
        Role role = resolveAndValidateRole(request.getRole());

        User newUser = buildNewUserFromRequest(request, role);
        userRepository.save(newUser);
    }

    public Map<String, Object> validateOrRefreshSession(HttpServletRequest request, HttpServletResponse response) {
        try {
            return validateAccessToken(request, response); // usa método centralizado
        } catch (InvalidTokenException | MissingTokenException e) {
            log.info("Access token inválido/ausente. Tentando refresh...");
        }

        try {
            refreshToken(request, response);
            return validateAccessToken(request, response); // agora sim com token renovado
        } catch (Exception ex) {
            log.warn("Falha ao renovar sessão: {}", ex.getMessage());
            clearAuthCookies(response);
            throw new InvalidTokenException("Sessão inválida. Faça login novamente.");
        }
    }

    public UserProfileDTO getCurrentUserProfile(User user) {
        if (user == null) {
            throw new InvalidTokenException("Usuário não autenticado.");
        }

        return UserProfileDTO.builder()
                .userId(user.getId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().getName())
                .twoFactorEnabled(user.isTwoFactorEnabled())
                .build();
    }


    /**
     * Auxliliares
     */

    private void checkLoginRateLimits(String ipAddress, String email) {
        Integer ipAttempts = loginAttemptsPerIp.getIfPresent(ipAddress);
        ipAttempts = (ipAttempts == null ? 0 : ipAttempts) + 1;
        loginAttemptsPerIp.put(ipAddress, ipAttempts);
        if (ipAttempts > MAX_ATTEMPTS_PER_MINUTE) {
            throw new RateLimitExceededException("Too many login attempts from this IP. Please try again later.");
        }

        Integer emailAttempts = loginAttemptsPerEmail.getIfPresent(email);
        emailAttempts = (emailAttempts == null ? 0 : emailAttempts) + 1;
        loginAttemptsPerEmail.put(email, emailAttempts);
        if (emailAttempts > MAX_ATTEMPTS_PER_EMAIL_PER_MINUTE) {
            throw new RateLimitExceededException("Too many login attempts for this account. Please try again later.");
        }
    }

    private void validateUserState(User user) {
        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("Please verify your email before logging in.");
        }

        if (user.getUserStatus() != null) {
            String status = user.getUserStatus().getStatus().toLowerCase();
            switch (status) {
                case "suspended" -> throw new AccountSuspendedException("Your account has been suspended. Please contact support.");
                case "deactivated" -> throw new AccountNotActiveException("Your account is deactivated. Please contact support.");
            }
        }

        if (user.isAccountLocked()) {
            if (user.getAccountLockedAt() != null &&
                    user.getAccountLockedAt().plusMinutes(15).isBefore(LocalDateTime.now())) {
                user.setAccountLocked(false);
                user.setLoginAttempts(0);
                user.setAccountLockedAt(null);
                userRepository.save(user);
            } else {
                throw new AccountLockedException("Your account is locked. Please try again later.");
            }
        }
    }

    private void handleInvalidPassword(User user) {
        int attempts = user.getLoginAttempts() + 1;
        user.setLoginAttempts(attempts);

        if (attempts >= 5 && !user.isAccountLocked()) {
            user.setAccountLocked(true);
            user.setAccountLockedAt(LocalDateTime.now());
            mailService.sendAccountLockedEmail(user.getEmail(), user.getUsername());
        }

        userRepository.save(user);

        if (user.isAccountLocked()) {
            throw new AccountLockedException("Your account is locked. Please try again later.");
        }

        throw new InvalidCredentialsException("The email or password you entered is incorrect.");
    }

    private Pending2FALogin createPending2FALogin(User user) {
        Pending2FALogin pending = new Pending2FALogin();
        pending.setId(UUID.randomUUID());
        pending.setUser(user);
        pending.setTempToken(UUID.randomUUID().toString());
        pending.setCreatedAt(LocalDateTime.now());
        pending.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        pending2FALoginRepository.save(pending);
        return pending;
    }

    private void recordLoginAttempt(User user, String ipAddress, String userAgent, boolean success, String reason) {
        LoginHistory history = new LoginHistory();
        history.setId(UUID.randomUUID());
        history.setUser(user);
        history.setLoginDate(LocalDateTime.now());
        history.setIpAddress(ipAddress);
        history.setLocation(metadataExtractor.getLocation(ipAddress));
        history.setDevice(metadataExtractor.detectDevice(userAgent));
        history.setBrowser(metadataExtractor.detectBrowser(userAgent));
        history.setOperatingSystem(metadataExtractor.detectOS(userAgent));
        history.setSuccess(success);
        history.setFailureReason(reason);
        loginHistoryRepository.save(history);
    }

    private String extractSessionIdFromRequest(HttpServletRequest request) {
        String token = jwtCookieUtil.extractTokenFromCookie(request);
        if (token == null || token.isBlank()) {
            throw new InvalidRequestException("Access token not found.");
        }

        try {
            Claims claims = jwtTokenProvider.extractClaims(token);
            String sessionId = claims.get("sessionId", String.class);
            if (sessionId == null || sessionId.isBlank()) {
                throw new InvalidRequestException("Session ID not found in token.");
            }
            return sessionId;
        } catch (JwtException e) {
            throw new InvalidRequestException("Invalid access token.");
        }
    }

    public void clearAuthCookies(HttpServletResponse response) {
        jwtCookieUtil.clearTokenCookie(response);
        jwtCookieUtil.clearRefreshTokenCookie(response);
    }

    private void checkRefreshRateLimit(String ipAddress) {
        int attempts = refreshAttemptsPerIp.get(ipAddress, k -> 0) + 1;
        refreshAttemptsPerIp.put(ipAddress, attempts);

        if (attempts > MAX_REFRESH_ATTEMPTS_PER_MINUTE) {
            throw new RateLimitExceededException("Muitas tentativas de refresh. Tente novamente em instantes.");
        }
    }

    private RefreshToken resolveValidRefreshToken(HttpServletRequest request) {
        String rawToken = jwtCookieUtil.extractRefreshTokenFromCookie(request);
        if (rawToken == null || rawToken.isBlank()) {
            throw new MissingTokenException("Refresh token não encontrado no cookie.");
        }

        RefreshToken token = refreshTokenRepository.findByToken(rawToken)
                .orElseThrow(() -> new RefreshTokenExpiredException("Refresh token inválido. Faça login novamente."));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenExpiredException("Refresh token expirado. Faça login novamente.");
        }

        return token;
    }

    private String resolveSessionId(HttpServletRequest request) {
        try {
            String token = jwtCookieUtil.extractTokenFromCookie(request);
            Claims claims = jwtTokenProvider.extractClaims(token);
            return claims.get("sessionId", String.class);
        } catch (Exception e) {
            return UUID.randomUUID().toString();
        }
    }

    private Duration getRefreshDuration(RefreshToken oldToken) {
        Duration existingDuration = Duration.between(oldToken.getCreatedAt(), oldToken.getExpiresAt());
        return existingDuration.toHours() >= 24
                ? Duration.ofMinutes(authProperties.getCookiesDurations().getRefreshLongMin())
                : Duration.ofMinutes(authProperties.getCookiesDurations().getRefreshShortMin());
    }

    private RefreshToken createNewRefreshToken(User user, Duration duration) {
        RefreshToken token = new RefreshToken();
        token.setId(UUID.randomUUID());
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plus(duration));
        return token;
    }

    private String issueTokensAndSetCookies(User user, String sessionId,
                                            RefreshToken refreshToken, Duration duration,
                                            HttpServletResponse response) {

        String accessToken = jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getName(),
                sessionId
        );

        jwtCookieUtil.setTokenCookie(response, accessToken);
        jwtCookieUtil.setRefreshTokenCookie(response, refreshToken.getToken(), duration);

        return accessToken;
    }

    private String createUserSession(User user, HttpServletRequest request) {
        String sessionId = UUID.randomUUID().toString();
        activeSessionService.createSession(user, sessionId, request);
        return sessionId;
    }

    private String generateAccessToken(User user, String sessionId) {
        return jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getName(),
                sessionId
        );
    }

    private Duration getRefreshDurationByRememberMe(boolean rememberMe) {
        return rememberMe
                ? Duration.ofMinutes(authProperties.getCookiesDurations().getRefreshLongMin())
                : Duration.ofMinutes(authProperties.getCookiesDurations().getRefreshShortMin());
    }

    private RefreshToken createAndStoreRefreshToken(User user, Duration duration) {
        refreshTokenRepository.deleteByUserId(user.getId());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID());
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshToken.setExpiresAt(LocalDateTime.now().plus(duration));
        refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    private String extractAccessTokenOrThrow(HttpServletRequest request) {
        String token = jwtCookieUtil.extractTokenFromCookie(request);
        if (token == null || token.isBlank()) {
            throw new MissingTokenException("Token não encontrado no cookie.");
        }
        return token;
    }

    private Map<String, Object> buildClaimsResponse(Claims claims) {
        return Map.of(
                "valid", true,
                "userId", claims.getSubject(),
                "email", claims.get("email"),
                "role", claims.get("role"),
                "expiresAt", claims.getExpiration()
        );
    }

    private void validateEmailAndUsernameUniqueness(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("The email is already in use.");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("The username is already in use.");
        }
    }

    private void validatePasswordStrength(String password) {
        if (!isStrongPassword(password)) {
            throw new PasswordTooWeakException("Password must be at least 8 characters, include uppercase, lowercase letters and a number.");
        }
    }

    private Role resolveAndValidateRole(String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));

        if (!ALLOWED_REGISTRATION_ROLES.contains(roleName.toUpperCase())) {
            throw new InvalidRoleAssignmentException("You are not allowed to register this type of account.");
        }

        return role;
    }

    private User buildNewUserFromRequest(RegisterRequest request, Role role) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setFullName(
                request.getFullName() != null && !request.getFullName().isBlank()
                        ? request.getFullName()
                        : request.getFirstName() + " " + request.getLastName()
        );
        user.setSocialName(request.getSocialName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setEmailVerified(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }











}
