package com.ecoprem.auth.security;

import com.ecoprem.auth.exception.AuthenticationException;
import com.ecoprem.user.repository.UserRepository;
import com.ecoprem.auth.service.ActiveSessionService;
import com.ecoprem.auth.util.LoginMetadataExtractor;
import com.ecoprem.core.audit.service.SecurityAuditService;
import com.ecoprem.entity.auth.ActiveSession;
import com.ecoprem.entity.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionAuthenticationProcessor {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final ActiveSessionService activeSessionService;
    private final LoginMetadataExtractor metadataExtractor;
    private final SecurityAuditService securityAuditService;

    public void authenticateFromToken(String token, HttpServletRequest request, HttpServletResponse response) {
        UUID userId = jwtTokenProvider.getUserIdFromJWT(token);
        String sessionId = jwtTokenProvider.getSessionIdFromJWT(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationException("Usuário não encontrado."));

        ActiveSession session = activeSessionService.findBySessionId(sessionId)
                .orElseThrow(() -> new AuthenticationException("Sessão não encontrada."));

        if (session.getExpiresAt() != null && session.getExpiresAt().isBefore(LocalDateTime.now())) {
            activeSessionService.terminateSession(sessionId);
            throw new AuthenticationException("Sessão expirada.");
        }

        if (!metadataExtractor.isSessionMetadataMatching(session, request)) {
            securityAuditService.logSuspiciousSession(user, request);
            throw new AuthenticationException("Sessão suspeita detectada.");
        }

        activeSessionService.updateLastAccessIfValid(sessionId, user);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user, null, null
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
