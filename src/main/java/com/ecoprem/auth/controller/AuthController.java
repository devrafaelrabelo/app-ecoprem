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

    private final JwtTokenProvider jwtTokenProvider;

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

        return ResponseEntity.ok("Login successful. Access token issued.");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal User user,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {

        String token = jwtCookieUtil.extractTokenFromCookie(request);
        if (token == null) {
            return ResponseEntity.badRequest().body("No token provided.");
        }

        authService.logout(user, token, request, response);

        return ResponseEntity.ok("Logged out successfully. Token revoked.");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = jwtCookieUtil.extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("No refresh token cookie found.");
        }

        LoginWithRefreshResponse result = authService.refreshToken(refreshToken, request, response);

        return ResponseEntity.ok("Token refreshed successfully. New access token issued.");
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
