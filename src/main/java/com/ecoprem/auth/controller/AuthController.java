package com.ecoprem.auth.controller;

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
import com.ecoprem.auth.service.RefreshTokenService;
import com.ecoprem.auth.service.RevokedTokenService;
import com.ecoprem.auth.util.LoginMetadataExtractor;
import com.ecoprem.common.ApiError;
import com.github.benmanes.caffeine.cache.Cache;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
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

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return JWT + refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Account locked, suspended or 2FA required",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   HttpServletRequest servletRequest,
                                   HttpServletResponse response) {

        log.info("Login received. rememberMe = {}", request.isRememberMe());

        LoginResult result = authService.login(request, servletRequest);
        LoginWithRefreshResponse loginResponse = result.response();
        User user = result.user();

        jwtCookieUtil.setTokenCookie(response, loginResponse.getAccessToken());

        if (request.isRememberMe()) {
            refreshTokenRepository.deleteByUserId(user.getId());

            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setId(UUID.randomUUID());
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setUser(user);
            refreshToken.setCreatedAt(LocalDateTime.now());
            refreshToken.setExpiresAt(LocalDateTime.now().plusDays(30));
            refreshTokenRepository.save(refreshToken);
            log.info(">> Enviando refreshToken no cookie...");
            jwtCookieUtil.setRefreshTokenCookie(response, refreshToken.getToken(), Duration.ofDays(30));
            activityLogService.logActivity(user, "Login realizado com rememberMe=" + request.isRememberMe(), servletRequest);
        } else {
            activityLogService.logActivity(user, "Login realizado sem rememberMe", servletRequest);
        }

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

        RefreshToken newRefresh = new RefreshToken();
        newRefresh.setId(UUID.randomUUID());
        newRefresh.setToken(UUID.randomUUID().toString());
        newRefresh.setUser(user);
        newRefresh.setCreatedAt(LocalDateTime.now());
        newRefresh.setExpiresAt(LocalDateTime.now().plusDays(30));
        refreshTokenRepository.save(newRefresh);

        String newAccessToken = jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getName()
        );

        jwtCookieUtil.setTokenCookie(response, newAccessToken);
        jwtCookieUtil.setRefreshTokenCookie(response, newRefresh.getToken(), Duration.ofDays(30));

        activityLogService.logActivity(user, "Refreshed token via cookie", request);

        return ResponseEntity.ok().build(); // token enviado por cookie
    }

}
