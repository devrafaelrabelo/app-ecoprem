package com.controlcenter.auth.util;

import com.controlcenter.auth.config.AuthProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
public class JwtCookieUtil {

    @Autowired
    private AuthProperties authProperties;

    private void addCookie(HttpServletResponse response, String name, String value, Duration duration, boolean httpOnly) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(httpOnly)
                .secure(authProperties.getCookiesProperties().isSecure())
                .path("/")
                .sameSite(authProperties.getCookiesProperties().getSameSite())
                .maxAge(duration)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearCookie(HttpServletResponse response, String name, boolean httpOnly) {
        addCookie(response, name, "", Duration.ZERO, httpOnly);
    }

    public void setTokenCookie(HttpServletResponse response, String token) {
        addCookie(response,
                authProperties.getCookieNames().getAccess(),
                token,
                Duration.ofMinutes(authProperties.getCookiesDurations().getAccessTokenMin()),
                authProperties.getCookiesProperties().isHttpOnly()
        );
    }

    public void clearTokenCookie(HttpServletResponse response) {
        clearCookie(response,
                authProperties.getCookieNames().getAccess(),
                authProperties.getCookiesProperties().isHttpOnly()
        );
    }

    public String extractTokenFromCookie(HttpServletRequest request) {
        return extractCookie(request, authProperties.getCookieNames().getAccess());
    }

    public void setRefreshTokenCookie(HttpServletResponse response, String token, Duration duration) {
        addCookie(response,
                authProperties.getCookieNames().getRefresh(),
                token,
                duration,
                authProperties.getCookiesProperties().isHttpOnly()
        );
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        clearCookie(response,
                authProperties.getCookieNames().getRefresh(),
                authProperties.getCookiesProperties().isHttpOnly()
        );
    }

    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        return extractCookie(request, authProperties.getCookieNames().getRefresh());
    }

    public void setTempTokenCookie(HttpServletResponse response, String tempToken, Duration duration) {
        addCookie(response,
                authProperties.getCookieNames().getTwofa(),
                tempToken,
                duration,
                true
        );
    }

    public void clearTempTokenCookie(HttpServletResponse response) {
        clearCookie(response,
                authProperties.getCookieNames().getTwofa(),
                true
        );
    }

    public Optional<String> extractTempTokenFromCookie(HttpServletRequest request) {
        String value = extractCookie(request, authProperties.getCookieNames().getTwofa());
        return Optional.ofNullable(value);
    }

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
