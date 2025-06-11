package com.ecoprem.auth.controller;

import com.ecoprem.auth.dto.ForgotPasswordRequest;
import com.ecoprem.auth.dto.ResetPasswordRequest;
import com.ecoprem.auth.repository.UserRepository;
import com.ecoprem.auth.service.MailService;
import com.ecoprem.auth.service.PasswordResetService;
import com.ecoprem.auth.service.RateLimiterService;
import com.ecoprem.entity.auth.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private final UserRepository userRepository;
    private final RateLimiterService rateLimiterService;
    private final MailService mailService;


    @Operation(
            summary = "Solicitar redefinição de senha",
            description = "Envia um e-mail com link de redefinição de senha se o e-mail informado estiver cadastrado e verificado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação registrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "E-mail não verificado ou inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        String key = "forgot:" + request.email().toLowerCase();

        if (!rateLimiterService.checkAndIncrement(key)) {
            log.warn("🚫 Rate limit excedido para o e-mail: {} ({} tentativas)", request.email(), rateLimiterService.getAttempts(key));
            return ResponseEntity.status(429).body("Muitas tentativas. Tente novamente em alguns minutos.");
        }

        // Continua com a lógica
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "E-mail não encontrado"));

        if (!user.isEmailVerified()) {
            return ResponseEntity.badRequest().body("E-mail não verificado");
        }

        String token = passwordResetService.createToken(user, 30);
        mailService.sendPasswordResetEmail(user.getEmail(), token);

        return ResponseEntity.ok("Solicitação registrada. Verifique seu e-mail.");
    }

    @Operation(
            summary = "Redefinir senha",
            description = "Permite redefinir a senha a partir de um token de redefinição válido. O token deve ter sido enviado por e-mail."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha redefinida com sucesso"),
            @ApiResponse(responseCode = "400", description = "Token expirado, inválido ou senha fraca"),
            @ApiResponse(responseCode = "404", description = "Token não encontrado")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok("Senha redefinida com sucesso.");
    }
}
