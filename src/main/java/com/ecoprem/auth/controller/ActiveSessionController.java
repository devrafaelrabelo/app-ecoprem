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
            dto.setSessionId(session.getSessionId() != null ? String.valueOf(UUID.fromString(session.getSessionId())) : null);
            dto.setDevice(session.getDevice());
            dto.setBrowser(session.getBrowser());
            dto.setOperatingSystem(session.getOperatingSystem());
            dto.setIpAddress(session.getIpAddress());
            dto.setCreatedAt(session.getCreatedAt());
            dto.setExpiresAt(session.getExpiresAt());
            return dto;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<?> terminateSession(@PathVariable String sessionId) {
        activeSessionService.terminateSession(sessionId);
        return ResponseEntity.ok("Session terminated");
    }
}
