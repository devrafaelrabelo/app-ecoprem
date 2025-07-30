package com.controlcenter.auth.service;

import com.controlcenter.auth.config.AuthProperties;
import com.controlcenter.auth.dto.LoginWithRefreshResponse;
import com.controlcenter.auth.security.JwtTokenProvider;
import com.controlcenter.auth.util.JwtCookieUtil;
import com.controlcenter.entity.auth.RefreshToken;
import com.controlcenter.entity.security.Permission;
import com.controlcenter.entity.user.User;
import com.controlcenter.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class LoginFinalizerService {

    private final JwtCookieUtil jwtCookieUtil;
    private final ActivityLogService activityLogService;
    private final UserService userService;
    private final TokenService tokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthProperties authProperties;

    public LoginWithRefreshResponse finalizeLogin(User user,
                                                  boolean rememberMe,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) {

        String sessionId = userService.createUserSession(user, request);

        String accessToken = generateAccessToken(user, sessionId);

        Duration refreshDuration = getRefreshDurationByRememberMe(rememberMe);
        RefreshToken refreshToken = tokenService.createAndStoreRefreshToken(user, sessionId, refreshDuration);

        jwtCookieUtil.setTokenCookie(response, accessToken);
        jwtCookieUtil.setRefreshTokenCookie(response, refreshToken.getToken(), refreshDuration);

        logActivityAsync(user, "Login realizado com rememberMe=" + rememberMe, request);

        return new LoginWithRefreshResponse(
                accessToken,
                refreshToken.getToken(),
                user.getUsername(),
                user.getFullName(),
                user.isTwoFactorEnabled()
        );
    }

    private void logActivityAsync(User user, String description, HttpServletRequest request) {
        CompletableFuture.runAsync(() ->
                activityLogService.logActivity(user, description, request)
        );
    }

    private Duration getRefreshDurationByRememberMe(boolean rememberMe) {
        return rememberMe
                ? Duration.ofMinutes(authProperties.getCookiesDurations().getRefreshLongMin())
                : Duration.ofMinutes(authProperties.getCookiesDurations().getRefreshShortMin());
    }

    private String generateAccessToken(User user, String sessionId) {
        List<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .distinct()
                .toList();

        return jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                permissions, // agora com permissions, não roles
                sessionId
        );
    }
}
