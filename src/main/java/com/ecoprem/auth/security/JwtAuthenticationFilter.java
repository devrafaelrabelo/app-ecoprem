package com.ecoprem.auth.security;

import com.ecoprem.auth.config.AuthPathProperties;
import com.ecoprem.auth.exception.AuthenticationException;
import com.ecoprem.auth.service.RevokedTokenService;
import com.ecoprem.auth.util.JwtCookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtCookieUtil jwtCookieUtil;
    private final SessionAuthenticationProcessor sessionAuthenticationProcessor;
    private final RevokedTokenService revokedTokenService;
    private final AuthPathProperties authPathProperties;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        log.debug("Intercepted request path: {}", path);

        if (authPathProperties.getPublicPaths().contains(path)) {
            log.debug("🔓 Skipping auth for public path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtCookieUtil.extractTokenFromCookie(request);
        if (token == null) {
            log.debug("No JWT token found in cookies.");
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtTokenProvider.isTokenValid(token)) {
            log.debug("Invalid JWT token.");
            filterChain.doFilter(request, response);
            return;
        }

        if (revokedTokenService.isTokenRevoked(token)) {
            log.warn("Token has been revoked: {}", token);
            respondUnauthorized(response, "Token has been revoked.");
            return;
        }

        try {
            log.debug("Token is valid. Authenticating session...");
            sessionAuthenticationProcessor.authenticateFromToken(token, request, response);
            log.debug("Authentication complete.");
        } catch (AuthenticationException ex) {
            log.warn("Authentication failed: {}", ex.getMessage());
            respondUnauthorized(response, ex.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void respondUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }


}
