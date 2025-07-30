package com.controlcenter.auth.controller;

import com.controlcenter.auth.dto.ActiveSessionResponse;
import com.controlcenter.entity.user.User;
import com.controlcenter.auth.service.ActiveSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Sessões Ativas", description = "Gerencie suas sessões de autenticação.")
@RestController
@RequestMapping("/api/auth/sessions")
@RequiredArgsConstructor
public class ActiveSessionController {

    private final ActiveSessionService activeSessionService;

    @Operation(summary = "Listar sessões ativas do usuário autenticado")
    @GetMapping
    public ResponseEntity<List<ActiveSessionResponse>> getSessions(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(activeSessionService.getSessionResponses(user));
    }

    @Operation(summary = "Encerrar todas as sessões do usuário autenticado")
    @DeleteMapping
    public ResponseEntity<?> terminateAllSessions(@AuthenticationPrincipal User user) {
        activeSessionService.terminateAllSessions(user);
        return ResponseEntity.ok("Todas as suas sessões foram encerradas.");
    }

    @Operation(summary = "Encerrar sessão específica do usuário autenticado")
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<?> terminateSession(@PathVariable @NotBlank String sessionId,
                                              @AuthenticationPrincipal User user) {
        boolean success = activeSessionService.terminateSessionIfOwned(sessionId, user);
        return success
                ? ResponseEntity.ok("Sessão encerrada com sucesso.")
                : ResponseEntity.status(403).body("Sessão não pertence a este usuário.");
    }
}
