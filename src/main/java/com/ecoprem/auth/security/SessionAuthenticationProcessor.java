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

        log.info("1ª authenticateFromToken");

        UUID userId = jwtTokenProvider.getUserIdFromJWT(token);
        String sessionId = jwtTokenProvider.getSessionIdFromJWT(token);

        log.info("2ª authenticateFromToken");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationException("Usuário não encontrado."));

        log.info("3ª authenticateFromToken");

        ActiveSession session = activeSessionService.findBySessionId(sessionId)
                .orElseThrow(() -> new AuthenticationException("Sessão não encontrada."));

        log.info("4ª authenticateFromToken");

        if (session.getExpiresAt() != null && session.getExpiresAt().isBefore(LocalDateTime.now())) {
            activeSessionService.terminateSession(sessionId);
            throw new AuthenticationException("Sessão expirada.");
        }

        log.info("5ª authenticateFromToken");

        if (!metadataExtractor.isSessionMetadataMatching(session, request)) {
            securityAuditService.logSuspiciousSession(user, request);
            throw new AuthenticationException("Sessão suspeita detectada.");
        }

        log.info("6ª authenticateFromToken");

        activeSessionService.updateLastAccessIfValid(sessionId, user);

        // 🔥 Novidade: carregar permissões e aplicar como authorities
        var permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(p -> new org.springframework.security.core.authority.SimpleGrantedAuthority(p.getName()))
                .distinct()
                .toList();

        log.info("7ª authenticateFromToken");

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user, null, permissions
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
