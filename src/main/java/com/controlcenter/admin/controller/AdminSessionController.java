package com.controlcenter.admin.controller;


import com.controlcenter.admin.dto.SessionInfoDTO;
import com.controlcenter.admin.dto.SessionRevokeAuditDTO;
import com.controlcenter.admin.service.AdminSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/admin/sessions")
@RequiredArgsConstructor
public class AdminSessionController {

    private final AdminSessionService adminSessionService;

    @GetMapping("/count")
    @Operation(
            summary = "Contar sessões ativas",
            description = "Retorna a quantidade total de sessões atualmente ativas no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Contagem de sessões retornada com sucesso"),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
            }
    )
    @PreAuthorize("hasAuthority('activesession:read')")
    public ResponseEntity<Long> countActiveSessions() {
        return ResponseEntity.ok(adminSessionService.countActiveSessions());
    }

    @GetMapping("/user/{userId}/last")
    @Operation(
            summary = "Obter última sessão de um usuário",
            description = "Retorna os dados da última sessão ativa de um usuário específico.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Última sessão encontrada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Usuário ou sessão não encontrada"),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
            }
    )
    @PreAuthorize("hasAuthority('activesession:read')")
    public ResponseEntity<SessionInfoDTO> getLastSession(@PathVariable UUID userId) {
        return ResponseEntity.ok(adminSessionService.findLastSessionByUser(userId));
    }

    @GetMapping("/expired")
    @Operation(
            summary = "Listar sessões expiradas",
            description = "Retorna todas as sessões que já expiraram no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de sessões expiradas retornada"),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
            }
    )
    @PreAuthorize("hasAuthority('activesession:read')")
    public ResponseEntity<List<SessionInfoDTO>> getExpiredSessions() {
        return ResponseEntity.ok(adminSessionService.findExpiredSessions());
    }

    @GetMapping("/audit-log")
    @Operation(
            summary = "Listar log de revogação de sessões",
            description = "Retorna um log de todas as sessões que foram revogadas, incluindo o responsável pela revogação.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Log de revogação retornado com sucesso"),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
            }
    )
    @PreAuthorize("hasAuthority('activesession:read')")
    public ResponseEntity<List<SessionRevokeAuditDTO>> getSessionRevokeAuditLog() {
        return ResponseEntity.ok(adminSessionService.getAuditLog());
    }

    @GetMapping
    @Operation(
            summary = "Listar todas as sessões",
            description = "Retorna todas as sessões ativas registradas no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de sessões ativas retornada com sucesso"),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
            }
    )
    @PreAuthorize("hasAuthority('activesession:read')")
    public ResponseEntity<List<SessionInfoDTO>> getAllSessions() {
        return ResponseEntity.ok(adminSessionService.listAllSessions());
    }

    @GetMapping("/by-user/{userId}")
    @Operation(
            summary = "Listar sessões por usuário",
            description = "Retorna todas as sessões ativas associadas a um determinado usuário.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de sessões do usuário retornada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
            }
    )
    @PreAuthorize("hasAuthority('activesession:read')")
    public ResponseEntity<List<SessionInfoDTO>> getSessionsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(adminSessionService.listSessionsByUser(userId));
    }

    @DeleteMapping("/revoke/{sessionId}")
    @Operation(
            summary = "Revogar sessão específica",
            description = "Revoga manualmente uma sessão ativa, associando a ação ao administrador autenticado.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Sessão revogada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Sessão não encontrada"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado"),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
            }
    )
    @PreAuthorize("hasAuthority('activesession:revoke')")
    public ResponseEntity<Void> revokeSessionById(@PathVariable UUID sessionId,
                                                  @AuthenticationPrincipal UserDetails admin) {
        adminSessionService.revokeSessionById(sessionId, admin.getUsername());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/revoke-all/{userId}")
    @Operation(
            summary = "Revogar todas as sessões de um usuário",
            description = "Revoga todas as sessões ativas do usuário informado, associando a ação ao administrador autenticado.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Todas as sessões revogadas com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado"),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
            }
    )
    @PreAuthorize("hasAuthority('activesession:revoke')")
    public ResponseEntity<Void> revokeAllSessionsOfUser(@PathVariable UUID userId,
                                                        @AuthenticationPrincipal UserDetails admin) {
        adminSessionService.revokeAllSessionsOfUser(userId, admin.getUsername());
        return ResponseEntity.noContent().build();
    }
}
