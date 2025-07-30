package com.controlcenter.core.audit.service;

import com.controlcenter.auth.util.LoginMetadataExtractor;
import com.controlcenter.entity.audit.SecurityAuditEvent;
import com.controlcenter.core.audit.repository.SecurityAuditEventRepository;
import com.controlcenter.entity.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityAuditService {

    private final LoginMetadataExtractor metadataExtractor;
    private final SecurityAuditEventRepository securityAuditEventRepository;

    @Transactional
    public void logSuspiciousSession(User user, HttpServletRequest request) {
        String ip = metadataExtractor.getClientIp(request);
        String userAgent = metadataExtractor.getUserAgent(request);
        String browser = metadataExtractor.detectBrowser(userAgent);
        String os = metadataExtractor.detectOS(userAgent);
        String device = metadataExtractor.detectDevice(userAgent);

        log.warn("""
            🚨 [SECURITY] Sessão suspeita detectada:
            - Usuário: {} ({})
            - IP: {}
            - Browser: {}
            - Sistema Operacional: {}
            - Dispositivo: {}
            - Timestamp: {}
            """,
                user.getUsername(), user.getEmail(),
                ip, browser, os, device,
                LocalDateTime.now()
        );

        SecurityAuditEvent event = new SecurityAuditEvent();
        event.setId(UUID.randomUUID());
        event.setUser(user);
        event.setEventType("SESSAO_SUSPEITA");
        event.setDescription("Login com IP, navegador ou dispositivo desconhecido");
        event.setIpAddress(ip);
        event.setUserAgent(userAgent);
        event.setTimestamp(LocalDateTime.now());

        securityAuditEventRepository.save(event);
    }

    @Transactional
    public void logAccessDenied(User user, HttpServletRequest request, String detail) {
        SecurityAuditEvent event = new SecurityAuditEvent();
        event.setUser(user);
        event.setUserId(user.getId());
        event.setUsername(user.getUsername());
        event.setEventType("ACCESS_DENIED");
        event.setPath(request.getRequestURI());
        event.setMethod(request.getMethod());
        event.setIpAddress(request.getRemoteAddr());
        event.setUserAgent(request.getHeader("User-Agent"));
        event.setDescription(detail);
        event.setTimestamp(LocalDateTime.now());

        securityAuditEventRepository.save(event);
    }
}
