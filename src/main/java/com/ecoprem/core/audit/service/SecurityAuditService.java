package com.ecoprem.core.audit.service;

import com.ecoprem.auth.util.LoginMetadataExtractor;
import com.ecoprem.entity.auth.User;
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

        // TODO futuramente: salvar em tabela de auditoria
        // securityEventRepository.save(new SecurityAuditEvent(...));
    }
}
