package com.controlcenter.auth.service;

import com.controlcenter.auth.dto.ActiveSessionResponse;
import com.controlcenter.entity.auth.ActiveSession;
import com.controlcenter.entity.user.User;
import com.controlcenter.auth.repository.ActiveSessionRepository;
import com.controlcenter.auth.util.LoginMetadataExtractor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActiveSessionService {

    private final ActiveSessionRepository activeSessionRepository;
    private final LoginMetadataExtractor metadataExtractor;

    public void createSession(User user, String sessionId, HttpServletRequest request) {
        String userAgent = metadataExtractor.getUserAgent(request);

        ActiveSession session = new ActiveSession();
        session.setId(UUID.randomUUID());
        session.setUser(user);
        session.setSessionId(sessionId);
        session.setDevice(metadataExtractor.detectDevice(userAgent));
        session.setBrowser(metadataExtractor.detectBrowser(userAgent));
        session.setOperatingSystem(metadataExtractor.detectOS(userAgent));
        session.setIpAddress(metadataExtractor.getClientIp(request));
        session.setCreatedAt(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusHours(12));

        activeSessionRepository.save(session);
    }

    public List<ActiveSessionResponse> getSessionResponses(User user) {
        return activeSessionRepository.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<ActiveSession> findBySessionId(String sessionId) {
        return activeSessionRepository.findBySessionId(sessionId);
    }

    private ActiveSessionResponse toResponse(ActiveSession session) {
        return ActiveSessionResponse.builder()
                .sessionId(safeUUID(session.getSessionId()))
                .device(session.getDevice())
                .browser(session.getBrowser())
                .operatingSystem(session.getOperatingSystem())
                .ipAddress(session.getIpAddress())
                .createdAt(session.getCreatedAt())
                .expiresAt(session.getExpiresAt())
                .deviceName(session.getBrowser() + " on " + session.getOperatingSystem())
                .build();
    }

    private UUID safeUUID(String value) {
        try {
            return value != null ? UUID.fromString(value) : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public void terminateSession(String sessionId) {
        activeSessionRepository.deleteBySessionId(sessionId);
    }

    @Transactional
    public void terminateAllSessions(User user) {
        activeSessionRepository.deleteByUserId(user.getId());
    }

    @Transactional
    public boolean terminateSessionIfOwned(String sessionId, User user) {
        Optional<ActiveSession> sessionOpt = activeSessionRepository.findBySessionId(sessionId);
        if (sessionOpt.isPresent() && sessionOpt.get().getUser().getId().equals(user.getId())) {
            activeSessionRepository.delete(sessionOpt.get());
            return true;
        }
        return false;
    }

    @Transactional
    public void updateLastAccessIfValid(String sessionId, User user) {
        activeSessionRepository.findBySessionId(sessionId).ifPresent(session -> {
            if (session.getUser().getId().equals(user.getId())) {
                if (session.getExpiresAt() != null && session.getExpiresAt().isBefore(LocalDateTime.now())) {
                    // Sessão expirada: remova ou ignore
                    activeSessionRepository.delete(session);
                    return;
                }

                session.setLastAccessAt(LocalDateTime.now());
                activeSessionRepository.save(session);
            }
        });
    }

    @Scheduled(cron = "0 0 * * * *") // Executa a cada hora em ponto
    @Transactional
    public void cleanExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        List<ActiveSession> expiredSessions = activeSessionRepository.findByExpiresAtBefore(now);

        if (!expiredSessions.isEmpty()) {
            activeSessionRepository.deleteAll(expiredSessions);
            log.info("🧹 Limpando {} sessões expiradas.", expiredSessions.size());
        }
    }
}
