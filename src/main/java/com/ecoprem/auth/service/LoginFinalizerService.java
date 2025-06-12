package com.ecoprem.auth.service;

import com.ecoprem.auth.config.AuthProperties;
import com.ecoprem.auth.dto.LoginWithRefreshResponse;
import com.ecoprem.auth.security.JwtTokenProvider;
import com.ecoprem.auth.util.JwtCookieUtil;
import com.ecoprem.entity.auth.RefreshToken;
import com.ecoprem.entity.user.User;
import com.ecoprem.entity.security.Role;
import com.ecoprem.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

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

        Duration duration = getRefreshDurationByRememberMe(rememberMe);
        RefreshToken refreshToken = tokenService.createAndStoreRefreshToken(user, sessionId, duration);

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

    private Duration getRefreshDurationByRememberMe(boolean rememberMe) {
        return rememberMe
                ? Duration.ofMinutes(authProperties.getCookiesDurations().getRefreshLongMin())
                : Duration.ofMinutes(authProperties.getCookiesDurations().getRefreshShortMin());
    }

    private String generateAccessToken(User user, String sessionId) {
        return jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRoles().stream().map(Role::getName).toList(),
                sessionId
        );
    }
}
