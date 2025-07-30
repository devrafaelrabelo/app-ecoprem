package com.controlcenter.core.security;

import com.controlcenter.core.audit.service.SecurityAuditService;
import com.controlcenter.entity.user.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalSecurityExceptionHandler {

    private final SecurityAuditService securityAuditService;

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(HttpServletRequest request, AccessDeniedException ex) {
        String path = request.getRequestURI();
        String username = "anonymous";
        User user = null;

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            if (principal instanceof User u) {
                user = u;
                username = u.getUsername();
            } else {
                username = principal.toString();
            }
        }

        log.warn("🚫 Acesso negado para usuário [{}] no endpoint [{}] - motivo: {}", username, path, ex.getMessage());

        // 🟡 Registra no banco
        if (user != null) {
            securityAuditService.logAccessDenied(user, request, ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "type", "https://api.controlcenter.com/errors/forbidden",
                        "title", "Acesso negado",
                        "status", 403,
                        "detail", "Você não tem permissão para acessar este recurso.",
                        "timestamp", LocalDateTime.now().toString(),
                        "path", path,
                        "user", username
                ));
    }
}
