package com.controlcenter.auth.security;

import com.controlcenter.auth.config.AuthPathProperties;
import com.controlcenter.auth.repository.ActiveSessionRepository;
import com.controlcenter.auth.service.RevokedTokenService;
import com.controlcenter.auth.util.JwtCookieUtil;
import com.controlcenter.exceptions.exception.AuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtCookieUtil jwtCookieUtil;
    private final SessionAuthenticationProcessor sessionAuthenticationProcessor;
    private final RevokedTokenService revokedTokenService;
    private final AuthPathProperties authPathProperties;
    private final ActiveSessionRepository activeSessionRepository;
    private final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        String path = request.getServletPath();
        log.info("Intercepted request path: {}", path);

        if (authPathProperties.getPublicPaths().contains(path)) {
            log.info("🔓 Skipping auth for public path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtCookieUtil.extractTokenFromCookie(request);
        String refreshToken = jwtCookieUtil.extractRefreshTokenFromCookie(request);

        if (token == null && refreshToken == null) {
            log.warn("🚫 Rejeitado: requisição sem access_token nem refresh_token para {}", path);
            respondUnauthorized(response, "Requisição sem token de autenticação.");
            return;
        }

        if (!jwtTokenProvider.isTokenValid(token)) {
            log.info("Invalid JWT token.");
            filterChain.doFilter(request, response);
            return;
        }

        if (revokedTokenService.isTokenRevoked(token)) {
            log.warn("Token has been revoked: {}", token);
            respondUnauthorized(response, "Token has been revoked.");
            return;
        }

        try {
            String sessionId = jwtTokenProvider.getSessionIdFromJWT(token);
            log.info("Validating sessionId: {}", sessionId);

            activeSessionRepository.findBySessionId(sessionId).ifPresentOrElse(session -> {
                LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
                if (session.getExpiresAt().isBefore(now)) {
                    log.warn("Session has expired: {}", sessionId);
                    throw new AuthenticationException("Sessão expirada.");
                }
            }, () -> {
                log.warn("Session not found for sessionId: {}", sessionId);
                throw new AuthenticationException("Sessão inválida.");
            });

            log.info("Token and session are valid. Proceeding with authentication.");
            sessionAuthenticationProcessor.authenticateFromToken(token, request, response);

        } catch (AuthenticationException ex) {
            log.info("Authentication failed: {}", ex.getMessage());
            respondUnauthorized(response, ex.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void respondUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String json = objectMapper.writeValueAsString(
                Map.of(
                        "type", "https://api.controlcenter.com/errors/unauthorized",
                        "title", "Unauthorized request",
                        "status", 401,
                        "detail", message,
                        "timestamp", LocalDateTime.now().toString()
                )
        );

        response.getWriter().write(json);
    }
}
