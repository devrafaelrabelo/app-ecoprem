package com.ecoprem.core.audit.service;

import com.ecoprem.auth.util.LoginMetadataExtractor;
import com.ecoprem.core.audit.entity.SecurityAuditEvent;
import com.ecoprem.core.audit.repository.SecurityAuditEventRepository;
import com.ecoprem.entity.user.User;
import jakarta.servlet.http.HttpServletRequest;
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
    private final SecurityAuditEventRepository securityEventRepository;


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

        securityEventRepository.save(event);
    }
}
