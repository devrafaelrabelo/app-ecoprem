package com.controlcenter.auth.security;

import com.controlcenter.exceptions.exception.AuthenticationException;
import com.controlcenter.user.repository.UserRepository;
import com.controlcenter.auth.service.ActiveSessionService;
import com.controlcenter.auth.util.LoginMetadataExtractor;
import com.controlcenter.core.audit.service.SecurityAuditService;
import com.controlcenter.entity.auth.ActiveSession;
import com.controlcenter.entity.user.User;
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

        User user = userRepository.findDetailedById(userId)
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

        // 🔥 Novidade: carregar permissões e aplicar como permissions
        var permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(p -> new org.springframework.security.core.authority.SimpleGrantedAuthority(p.getName()))
                .distinct()
                .toList();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user, null, permissions
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
