package com.ecoprem.auth.util;

import com.ecoprem.auth.config.AuthProperties;
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

    public void setTokenCookie(HttpServletResponse response, String token) {
        System.out.println(authProperties.getCookieNames().getAccess());
        ResponseCookie cookie = ResponseCookie.from(authProperties.getCookieNames().getAccess(), token)
                .httpOnly(authProperties.getCookiesProperties().isHttpOnly())
                .secure(authProperties.getCookiesProperties().isSecure())
                .path("/")
                .sameSite(authProperties.getCookiesProperties().getSameSite())
                .maxAge(Duration.ofMinutes(authProperties.getCookiesDurations().getAccessTokenMin()))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clearTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(authProperties.getCookieNames().getAccess(), "")
                .httpOnly(authProperties.getCookiesProperties().isHttpOnly())
                .secure(authProperties.getCookiesProperties().isSecure())
                .path("/")
                .sameSite(authProperties.getCookiesProperties().getSameSite())
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (authProperties.getCookieNames().getAccess().equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public void setRefreshTokenCookie(HttpServletResponse response, String token, Duration duration) {
        ResponseCookie cookie = ResponseCookie.from(authProperties.getCookieNames().getRefresh(), token)
                .httpOnly(authProperties.getCookiesProperties().isHttpOnly())
                .secure(authProperties.getCookiesProperties().isSecure())
                .path("/")
                .sameSite(authProperties.getCookiesProperties().getSameSite())
                .maxAge(duration)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(authProperties.getCookieNames().getRefresh(), "")
                .httpOnly(authProperties.getCookiesProperties().isHttpOnly())
                .secure(authProperties.getCookiesProperties().isSecure())
                .path("/")
                .sameSite(authProperties.getCookiesProperties().getSameSite())
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (authProperties.getCookieNames().getRefresh().equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public void setTempTokenCookie(HttpServletResponse response, String tempToken, Duration duration) {
        ResponseCookie cookie = ResponseCookie.from(authProperties.getCookieNames().getTwofa(), tempToken)
                .httpOnly(true)
                .secure(authProperties.getCookiesProperties().isSecure())
                .path("/")
                .sameSite(authProperties.getCookiesProperties().getSameSite())
                .maxAge(duration)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clearTempTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(authProperties.getCookieNames().getTwofa(), "")
                .httpOnly(true)
                .secure(authProperties.getCookiesProperties().isSecure())
                .path("/")
                .sameSite(authProperties.getCookiesProperties().getSameSite())
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public Optional<String> extractTempTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return Optional.empty();
        for (Cookie cookie : request.getCookies()) {
            if (authProperties.getCookieNames().getTwofa().equals(cookie.getName())) {
                return Optional.ofNullable(cookie.getValue());
            }
        }
        return Optional.empty();
    }
}
