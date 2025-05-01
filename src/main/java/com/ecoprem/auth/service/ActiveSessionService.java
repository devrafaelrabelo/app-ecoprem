package com.ecoprem.auth.service;

import com.ecoprem.auth.entity.ActiveSession;
import com.ecoprem.auth.entity.User;
import com.ecoprem.auth.repository.ActiveSessionRepository;
import com.ecoprem.auth.util.LoginMetadataExtractor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
        // Opcional: define expiração
        session.setExpiresAt(LocalDateTime.now().plusHours(12));

        activeSessionRepository.save(session);
    }

    public List<ActiveSession> getSessions(User user) {
        return activeSessionRepository.findByUserId(user.getId());
    }

    public void terminateSession(String sessionId) {
        activeSessionRepository.deleteBySessionId(sessionId);
    }
}
