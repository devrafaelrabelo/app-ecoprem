package com.controlcenter.auth.service;

import com.controlcenter.auth.repository.ActiveSessionRepository;
import com.controlcenter.auth.util.JwtCookieUtil;
import com.controlcenter.auth.util.LoginMetadataExtractor;
import com.controlcenter.entity.auth.ActiveSession;
import com.controlcenter.entity.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SessionService {

    private final ActiveSessionRepository activeSessionRepository;
    private final LoginMetadataExtractor metadataExtractor;
    private final JwtCookieUtil jwtCookieUtil;
    private final JwtService jwtTokenProvider;

    public String createSession(User user, HttpServletRequest request) {
        String sessionId = UUID.randomUUID().toString();
        String ip = metadataExtractor.getClientIp(request);
        String userAgent = metadataExtractor.getUserAgent(request);

        ActiveSession session = new ActiveSession();
        session.setId(UUID.randomUUID());
        session.setUser(user);
        session.setSessionId(sessionId);
        session.setIpAddress(ip);
        session.setDevice(metadataExtractor.detectDevice(userAgent));
        session.setBrowser(metadataExtractor.detectBrowser(userAgent));
        session.setOperatingSystem(metadataExtractor.detectOS(userAgent));
        session.setCreatedAt(LocalDateTime.now());
        session.setLastAccessAt(LocalDateTime.now());

        activeSessionRepository.save(session);
        return sessionId;
    }

    public Optional<ActiveSession> findBySessionId(String sessionId) {
        return activeSessionRepository.findBySessionId(sessionId);
    }

    public void updateLastAccess(String sessionId) {
        activeSessionRepository.findBySessionId(sessionId).ifPresent(session -> {
            session.setLastAccessAt(LocalDateTime.now());
            activeSessionRepository.save(session);
        });
    }

    public void deleteSession(String sessionId) {
        activeSessionRepository.findBySessionId(sessionId).ifPresent(activeSessionRepository::delete);
    }

    public String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null) ? forwarded.split(",")[0] : request.getRemoteAddr();
    }

    public String extractSessionIdFromRequest(HttpServletRequest request) {
        String token = jwtCookieUtil.extractTokenFromCookie(request);
        if (token == null || token.isBlank()) {
            log.warn("⚠️ Access token ausente ao tentar extrair sessionId.");
            return null;
        }

        return extractSessionIdFromToken(token);
    }

    public String extractSessionIdFromToken(String token) {
        try {
            Claims claims = jwtTokenProvider.extractClaims(token);
            return claims.get("sessionId", String.class);
        } catch (JwtException e) {
            log.warn("⚠️ Token inválido ao extrair sessionId: {}", e.getMessage());
            return null;
        }
    }
}
