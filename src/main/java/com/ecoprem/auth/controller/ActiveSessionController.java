package com.ecoprem.auth.controller;

import com.ecoprem.auth.entity.ActiveSession;
import com.ecoprem.auth.entity.User;
import com.ecoprem.auth.service.ActiveSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/sessions")
@RequiredArgsConstructor
public class ActiveSessionController {

    private final ActiveSessionService activeSessionService;

    @GetMapping
    public ResponseEntity<List<ActiveSession>> getSessions(@AuthenticationPrincipal User user) {
        List<ActiveSession> sessions = activeSessionService.getSessions(user);
        return ResponseEntity.ok(sessions);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<?> terminateSession(@PathVariable String sessionId) {
        activeSessionService.terminateSession(sessionId);
        return ResponseEntity.ok("Session terminated");
    }
}
