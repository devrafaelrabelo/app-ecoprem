package com.controlcenter.auth.service;

import com.controlcenter.auth.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtTokenProvider jwtTokenProvider;

    public String generateAccessToken(UUID userId, String email, List<String> permissions, String sessionId) {
        return jwtTokenProvider.generateToken(userId, email, permissions, sessionId);
    }

    public Claims extractClaims(String token) {
        return jwtTokenProvider.extractClaims(token);
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extractClaims(token);
            return jwtTokenProvider.isTokenExpired(claims);
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }

    public UUID getUserIdFromToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return UUID.fromString(claims.getSubject());
        } catch (Exception e) {
            throw new JwtException("Token inválido para extração de userId.");
        }
    }

    public String getSessionIdFromToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.get("sessionId", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public LocalDateTime getExpirationDate(String token) {
        return jwtTokenProvider.getExpirationDateFromJWT(token);
    }

    public Duration getRemainingTime(String token) {
        LocalDateTime expiresAt = getExpirationDate(token);
        return Duration.between(LocalDateTime.now(), expiresAt);
    }
}
