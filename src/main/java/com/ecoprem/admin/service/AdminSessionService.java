package com.ecoprem.admin.service;

import com.ecoprem.admin.dto.SessionDTO;
import com.ecoprem.admin.dto.SessionInfoDTO;
import com.ecoprem.admin.dto.SessionRevokeAuditDTO;
import com.ecoprem.auth.exception.ActiveSessionNotFoundException;
import com.ecoprem.auth.exception.UserNotFoundException;
import com.ecoprem.auth.repository.ActiveSessionRepository;
import com.ecoprem.auth.repository.RevokedTokenRepository;
import com.ecoprem.auth.util.LoginMetadataExtractor;
import com.ecoprem.entity.auth.ActiveSession;
import com.ecoprem.entity.auth.RevokedToken;
import com.ecoprem.entity.user.User;
import com.ecoprem.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminSessionService {

    private final ActiveSessionRepository activeSessionRepository;
    private final RevokedTokenRepository revokedTokenRepository;
    private final UserRepository userRepository;
    private final LoginMetadataExtractor loginMetadataExtractor;

    public long countActiveSessions() {
        return activeSessionRepository.count();
    }

    public SessionInfoDTO findLastSessionByUser(UUID userId) {
        return activeSessionRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(this::toInfoDTO)
                .orElseThrow(() -> new ActiveSessionNotFoundException("Última sessão não encontrada para usuário."));
    }

    public List<SessionInfoDTO> findExpiredSessions() {
        return activeSessionRepository.findByExpiresAtBefore(LocalDateTime.now()).stream()
                .map(this::toInfoDTO)
                .collect(Collectors.toList());
    }

    public List<SessionRevokeAuditDTO> getAuditLog() {
        return revokedTokenRepository.findAll().stream()
                .map(revoked -> {
                    SessionRevokeAuditDTO dto = new SessionRevokeAuditDTO();
                    dto.setSessionId(UUID.fromString(revoked.getSessionId())); // se sessionId for UUID como String
                    dto.setUserId(revoked.getUser().getId());
                    dto.setUsername(revoked.getUser().getUsername());
                    dto.setRevokedAt(revoked.getRevokedAt());
                    dto.setReason(revoked.getReason());
                    dto.setRevokedBy(revoked.getRevokedBy());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<SessionInfoDTO> listAllSessions() {
        return activeSessionRepository.findAll().stream()
                .map(this::toInfoDTO)
                .collect(Collectors.toList());
    }

    public List<SessionInfoDTO> listSessionsByUser(UUID userId) {
        return activeSessionRepository.findByUserId(userId).stream()
                .map(this::toInfoDTO)
                .collect(Collectors.toList());
    }

    public void revokeSessionById(UUID sessionId, String adminUsername) {
        ActiveSession session = activeSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ActiveSessionNotFoundException("Sessão não encontrada"));

        revokeSession(session, "Revogada manualmente por " + adminUsername);
    }

    public void revokeAllSessionsOfUser(UUID userId, String adminUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        List<ActiveSession> sessions = activeSessionRepository.findByUserId(userId);
        for (ActiveSession session : sessions) {
            revokeSession(session, "Todas as sessões revogadas por " + adminUsername);
        }
    }

    private void revokeSession(ActiveSession session, String reason) {
        RevokedToken revoked = new RevokedToken();
        revoked.setId(UUID.randomUUID());
        revoked.setToken("(n/a)"); // não armazenamos o token JWT original aqui
        revoked.setSessionId(session.getSessionId());
        revoked.setUser(session.getUser());
        revoked.setRevokedAt(LocalDateTime.now());
        revoked.setExpiresAt(session.getExpiresAt());
        revoked.setReason(reason);
        revokedTokenRepository.save(revoked);
        activeSessionRepository.delete(session);
    }

    private SessionInfoDTO toInfoDTO(ActiveSession session) {
        return SessionInfoDTO.builder()
                .sessionId(session.getSessionId())
                .userId(session.getUser().getId())
                .username(session.getUser().getUsername())
                .ipAddress(session.getIpAddress())
                .device(session.getDevice())
                .browser(session.getBrowser())
                .createdAt(session.getCreatedAt())
                .lastAccessAt(session.getLastAccessAt())
                .expiresAt(session.getExpiresAt())
                .expired(session.getExpiresAt() != null && session.getExpiresAt().isBefore(LocalDateTime.now()))
                .build();
    }

    private SessionDTO toDTO(ActiveSession session) {
        return new SessionDTO(
                session.getId(),
                session.getSessionId(),
                session.getDevice(),
                session.getBrowser(),
                session.getOperatingSystem(),
                session.getIpAddress(),
                session.getCreatedAt(),
                session.getLastAccessAt(),
                session.getExpiresAt()
        );
    }
}