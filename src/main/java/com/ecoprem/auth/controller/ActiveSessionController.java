package com.ecoprem.auth.controller;

import com.ecoprem.auth.dto.ActiveSessionResponse;
import com.ecoprem.auth.entity.ActiveSession;
import com.ecoprem.auth.entity.User;
import com.ecoprem.auth.service.ActiveSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/sessions")
@RequiredArgsConstructor
public class ActiveSessionController {


    private final ActiveSessionService activeSessionService;

    @GetMapping
    public ResponseEntity<List<ActiveSessionResponse>> getSessions(@AuthenticationPrincipal User user) {
        List<ActiveSession> sessions = activeSessionService.getSessions(user);

        List<ActiveSessionResponse> response = sessions.stream().map(session -> {
            ActiveSessionResponse dto = new ActiveSessionResponse();
            dto.setSessionId(session.getSessionId() != null ? UUID.fromString(session.getSessionId()) : null);
            dto.setDevice(session.getDevice());
            dto.setBrowser(session.getBrowser());
            dto.setOperatingSystem(session.getOperatingSystem());
            dto.setIpAddress(session.getIpAddress());
            dto.setCreatedAt(session.getCreatedAt());
            dto.setExpiresAt(session.getExpiresAt());

            String deviceName = session.getBrowser() + " on " + session.getOperatingSystem();
            dto.setDeviceName(deviceName);

            return dto;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<?> terminateAllSessions(@AuthenticationPrincipal User user) {
        activeSessionService.terminateAllSessions(user);
        return ResponseEntity.ok("Todas as suas sessões foram encerradas.");
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<?> terminateSession(@PathVariable String sessionId,
                                              @AuthenticationPrincipal User user) {
        boolean success = activeSessionService.terminateSessionIfOwned(sessionId, user);
        if (success) {
            return ResponseEntity.ok("Sessão encerrada com sucesso.");
        } else {
            return ResponseEntity.status(403).body("Sessão não pertence a este usuário.");
        }
    }
}
