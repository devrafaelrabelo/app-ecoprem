package com.ecoprem.auth.controller;

import com.ecoprem.auth.config.AuthProperties;
import com.ecoprem.auth.dto.*;
import com.ecoprem.auth.entity.RefreshToken;
import com.ecoprem.auth.entity.User;
import com.ecoprem.auth.exception.RateLimitExceededException;
import com.ecoprem.auth.exception.RefreshTokenExpiredException;
import com.ecoprem.auth.repository.RefreshTokenRepository;
import com.ecoprem.auth.service.ActivityLogService;
import com.ecoprem.auth.util.JwtCookieUtil;
import com.ecoprem.auth.security.JwtTokenProvider;
import com.ecoprem.auth.service.AuthService;
import com.ecoprem.auth.service.RevokedTokenService;
import com.ecoprem.auth.util.LoginMetadataExtractor;
import com.github.benmanes.caffeine.cache.Cache;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RevokedTokenService revokedTokenService;
    private final ActivityLogService activityLogService;
    private final LoginMetadataExtractor metadataExtractor;
    private final Cache<String, Integer> refreshAttemptsPerIp;

    private static final int MAX_REFRESH_ATTEMPTS_PER_MINUTE = 20;

    @Autowired
    private JwtCookieUtil jwtCookieUtil;

    @Autowired
    private AuthProperties authProperties;

    @GetMapping("/DevTest")
    public ResponseEntity<Map<String, Object>> getAuthConfig() {
        Map<String, Object> result = new HashMap<>();

        AuthProperties.Durations durations = authProperties.getCookiesDurations();
        AuthProperties.CookieProperties props = authProperties.getCookiesProperties();
        AuthProperties.CookieNames names = authProperties.getCookieNames();

        result.put("accessTokenMin", durations.getAccessTokenMin());
        result.put("refreshShortMin", durations.getRefreshShortMin());
        result.put("refreshLongMin", durations.getRefreshLongMin());

        result.put("secure", props.isSecure());
        result.put("httpOnly", props.isHttpOnly());
        result.put("sameSite", props.getSameSite());

        result.put("cookieNameAccess", names.getAccess());
        result.put("cookieNameRefresh", names.getRefresh());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   HttpServletRequest servletRequest,
                                   HttpServletResponse response) {

        log.info("Login received. rememberMe = {}", request.isRememberMe());

        LoginResult result = authService.login(request, servletRequest);

        LoginWithRefreshResponse loginResponse = authService.completeLogin(
                result.user(),
                request.isRememberMe(),
                servletRequest,
                response
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal User user,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {

        String token = jwtCookieUtil.extractTokenFromCookie(request);
        if (token == null) {
            return ResponseEntity.badRequest().body("No token provided.");
        }

        LocalDateTime expiresAt = jwtTokenProvider.getExpirationDateFromJWT(token);
        revokedTokenService.revokeToken(token, user, expiresAt);
        refreshTokenRepository.deleteByUserId(user.getId());
        jwtCookieUtil.clearTokenCookie(response);
        jwtCookieUtil.clearRefreshTokenCookie(response);

        return ResponseEntity.ok("Logged out successfully. Token revoked.");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String ipAddress = metadataExtractor.getClientIp(request);
        int refreshAttempts = refreshAttemptsPerIp.get(ipAddress, k -> 0) + 1;
        refreshAttemptsPerIp.put(ipAddress, refreshAttempts);
        if (refreshAttempts > MAX_REFRESH_ATTEMPTS_PER_MINUTE) {
            throw new RateLimitExceededException("Too many refresh attempts. Please try again later.");
        }

        String refreshToken = jwtCookieUtil.extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("No refresh token cookie found.");
        }

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RefreshTokenExpiredException("Invalid refresh token. Please login again."));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenExpiredException("Refresh token expired. Please login again.");
        }

        User user = token.getUser();
        refreshTokenRepository.deleteByUserId(user.getId());

        String accessToken = jwtCookieUtil.extractTokenFromCookie(request);
        String sessionId;
        try {
            Claims claims = jwtTokenProvider.extractClaims(accessToken);
            sessionId = claims.get("sessionId", String.class);
        } catch (Exception e) {
            sessionId = UUID.randomUUID().toString();
        }

        Duration existingDuration = Duration.between(token.getCreatedAt(), token.getExpiresAt());
        Duration duration = existingDuration.toHours() >= 24
                ? Duration.ofMinutes(authProperties.getCookiesDurations().getRefreshLongMin())
                : Duration.ofMinutes(authProperties.getCookiesDurations().getRefreshShortMin());

        RefreshToken newRefresh = new RefreshToken();
        newRefresh.setId(UUID.randomUUID());
        newRefresh.setToken(UUID.randomUUID().toString());
        newRefresh.setUser(user);
        newRefresh.setCreatedAt(LocalDateTime.now());
        newRefresh.setExpiresAt(LocalDateTime.now().plus(duration));
        refreshTokenRepository.save(newRefresh);

        String newAccessToken = jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getName(),
                sessionId
        );

        jwtCookieUtil.setTokenCookie(response, newAccessToken);
        jwtCookieUtil.setRefreshTokenCookie(response, newRefresh.getToken(), duration);

        activityLogService.logActivity(user, "Refreshed token via cookie", request);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = jwtCookieUtil.extractTokenFromCookie(request);

            if (token == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("valid", false, "error", "Token não encontrado no cookie"));
            }

            Claims claims = jwtTokenProvider.extractClaims(token);

            Map<String, Object> result = new HashMap<>();
            result.put("valid", true);
            result.put("userId", claims.getSubject());
            result.put("email", claims.get("email"));
            result.put("role", claims.get("role"));
            result.put("expiresAt", claims.getExpiration());

            return ResponseEntity.ok(result);

        } catch (JwtException | IllegalArgumentException e) {
            jwtCookieUtil.clearTokenCookie(response);
            jwtCookieUtil.clearRefreshTokenCookie(response);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "error", "Token inválido ou expirado"));
        }
    }
}
