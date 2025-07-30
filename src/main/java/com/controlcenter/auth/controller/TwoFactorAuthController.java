package com.controlcenter.auth.controller;


import com.controlcenter.auth.dto.*;

import com.controlcenter.entity.user.User;
import com.controlcenter.auth.repository.Pending2FALoginRepository;
import com.controlcenter.auth.service.AuthService;
import com.controlcenter.auth.service.BackupCodeService;
import com.controlcenter.auth.service.TwoFactorAuthService;

import com.controlcenter.auth.util.JwtCookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Tag(name = "Two-Factor Auth", description = "Gerenciamento de autenticação em dois fatores (2FA)")
@RestController
@RequestMapping("/api/auth/2fa")
@RequiredArgsConstructor
public class TwoFactorAuthController {

    private final TwoFactorAuthService twoFactorAuthService;
    private final Pending2FALoginRepository pending2FALoginRepository;
    private final BackupCodeService backupCodeService;
    private final AuthService authService;
    private final JwtCookieUtil jwtCookieUtil;

    // Setup: Gera secret + QR code
    @Operation(
            summary = "Inicia a configuração do 2FA para o usuário autenticado",
            description = "Gera uma chave secreta e QR Code compatível com aplicativos como Google Authenticator.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Configuração de 2FA criada com sucesso",
            content = @Content(schema = @Schema(implementation = TwoFactorSetupResponse.class))
    )
    @PostMapping("/setup")
    public ResponseEntity<TwoFactorSetupResponse> setup2FA(@AuthenticationPrincipal User user) {
        TwoFactorSetupResponse response = twoFactorAuthService.setupTwoFactorForUser(user);
        return ResponseEntity.ok(response);
    }



    @Operation(
            summary = "Verifica o código de 2FA e ativa o recurso para o usuário",
            description = "Confirma se o código fornecido está correto e ativa o 2FA permanentemente para o usuário autenticado.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "2FA ativado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Código inválido fornecido")
    })
    @PostMapping("/verify")
    public ResponseEntity<?> verify2FA(@AuthenticationPrincipal User user, @Valid @RequestBody TwoFactorVerifyRequest request) {
        boolean success = twoFactorAuthService.verifyAndEnableTwoFactor(user, request.getCode());

        if (success) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Autenticação em dois fatores ativada com sucesso."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", "Código inválido. Verifique e tente novamente."));
        }
    }



    @Operation(
            summary = "Desativa a autenticação em dois fatores (2FA)",
            description = "Remove o segredo 2FA, desativa a proteção adicional e apaga os códigos de backup existentes.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "2FA desativado com sucesso"),
    })
    @PostMapping("/disable")
    public ResponseEntity<?> disableTwoFactor(@AuthenticationPrincipal User user) {
        twoFactorAuthService.disableTwoFactor(user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Autenticação em dois fatores desativada e códigos de backup removidos."
        ));
    }


    @Operation(
            summary = "Valida o código de 2FA durante o processo de login",
            description = "Permite o login completo ao validar o código 2FA (TOTP ou backup) e emite tokens de sessão.",
            tags = {"Two-Factor Auth"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Código 2FA e token temporário de login",
                    required = true
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login com 2FA validado com sucesso",
                    content = @Content(schema = @Schema(implementation = LoginWithRefreshResponse.class))),
            @ApiResponse(responseCode = "400", description = "Código inválido ou token expirado"),
            @ApiResponse(responseCode = "401", description = "Token 2FA inválido ou expirado")
    })
    @PostMapping("/validate-login")
    public ResponseEntity<?> validateLogin2FA(@Valid @RequestBody TwoFactorLoginRequest request,
                                              HttpServletRequest httpRequest,
                                              HttpServletResponse response) {
        LoginWithRefreshResponse loginResponse = twoFactorAuthService.validate2FALogin(request, httpRequest, response);
        return ResponseEntity.ok(loginResponse);
    }


    @Operation(
            summary = "Lista os códigos de backup do 2FA do usuário",
            description = "Retorna todos os códigos de backup gerados, indicando se foram usados ou não.",
            security = @SecurityRequirement(name = "bearer-key"),
            tags = {"Two-Factor Auth"}
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de códigos de backup retornada com sucesso",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BackupCodeResponse.class)))
    )
    @GetMapping("/backup-codes")
    public ResponseEntity<List<BackupCodeResponse>> listBackupCodes(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(twoFactorAuthService.listBackupCodes(user));
    }


    @Operation(
            summary = "Gera novos códigos de backup para o 2FA",
            description = "Gera uma nova lista de códigos de backup e invalida os antigos.",
            security = @SecurityRequirement(name = "bearer-key"),
            tags = {"Two-Factor Auth"}
    )
    @ApiResponse(
            responseCode = "200",
            description = "Códigos de backup gerados com sucesso",
            content = @Content(array = @ArraySchema(schema = @Schema(example = "ABC123DEF")))
    )
    @PostMapping("/backup-codes/generate")
    public ResponseEntity<List<String>> generateBackupCodes(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(twoFactorAuthService.generateBackupCodes(user, 10));
    }


    @Operation(
            summary = "Remove todos os códigos de backup do usuário",
            description = "Deleta permanentemente todos os códigos de backup associados ao usuário autenticado.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Códigos de backup removidos com sucesso")
    })
    @DeleteMapping("/backup-codes")
    public ResponseEntity<?> deleteBackupCodes(@AuthenticationPrincipal User user) {
        twoFactorAuthService.deleteAllBackupCodes(user);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Todos os códigos de backup foram removidos com sucesso."
        ));
    }
}
