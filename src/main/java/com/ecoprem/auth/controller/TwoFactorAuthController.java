package com.ecoprem.auth.controller;

import com.ecoprem.auth.dto.*;
import com.ecoprem.entity.auth.BackupCode;
import com.ecoprem.entity.auth.Pending2FALogin;
import com.ecoprem.entity.auth.User;
import com.ecoprem.auth.exception.Expired2FATokenException;
import com.ecoprem.auth.exception.Invalid2FACodeException;
import com.ecoprem.auth.exception.Invalid2FATokenException;
import com.ecoprem.auth.repository.Pending2FALoginRepository;
import com.ecoprem.auth.repository.RefreshTokenRepository;
import com.ecoprem.auth.security.JwtTokenProvider;
import com.ecoprem.auth.service.ActiveSessionService;
import com.ecoprem.auth.service.AuthService;
import com.ecoprem.auth.service.BackupCodeService;
import com.ecoprem.auth.service.TwoFactorAuthService;
import com.ecoprem.auth.repository.UserRepository;
import com.ecoprem.auth.util.JwtCookieUtil;
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

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;


@Tag(name = "Two-Factor Auth", description = "Gerenciamento de autenticação em dois fatores (2FA)")
@RestController
@RequestMapping("/api/auth/2fa")
@RequiredArgsConstructor
public class TwoFactorAuthController {

    private final TwoFactorAuthService twoFactorAuthService;
    private final UserRepository userRepository;
    private final Pending2FALoginRepository pending2FALoginRepository;
    private final BackupCodeService backupCodeService;
    private final AuthService authService;

    // Setup: Gera secret + QR code
    @PostMapping("/setup")
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
    public ResponseEntity<TwoFactorSetupResponse> setup2FA(@AuthenticationPrincipal User user) throws Exception {
        String secret = twoFactorAuthService.generateSecret();
        String otpAuthUrl = twoFactorAuthService.getOtpAuthURL("EcoPrem", user.getEmail(), secret);

        byte[] qrImage = twoFactorAuthService.generateQRCodeImage(otpAuthUrl, 300, 300);
        String qrCodeImageBase64 = Base64.getEncoder().encodeToString(qrImage);

        user.setTwoFactorSecret(secret);
        userRepository.save(user);

        TwoFactorSetupResponse response = new TwoFactorSetupResponse();
        response.setSecret(secret);
        response.setQrCodeImageBase64(qrCodeImageBase64);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/verify")
    @Operation(
            summary = "Verifica o código de 2FA e ativa o recurso para o usuário",
            description = "Confirma se o código fornecido está correto e ativa o 2FA permanentemente para o usuário autenticado.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "2FA ativado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Código inválido fornecido")
    })
    public ResponseEntity<?> verify2FA(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TwoFactorVerifyRequest request
    ) {
        boolean isValid = twoFactorAuthService.verifyCode(user.getTwoFactorSecret(), request.getCode());

        if (isValid) {
            user.setTwoFactorEnabled(true);
            userRepository.save(user);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Autenticação em dois fatores ativada com sucesso."
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Código inválido. Verifique e tente novamente."
            ));
        }
    }


    @PostMapping("/disable")
    @Operation(
            summary = "Desativa a autenticação em dois fatores (2FA)",
            description = "Remove o segredo 2FA, desativa a proteção adicional e apaga os códigos de backup existentes.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "2FA desativado com sucesso"),
    })
    public ResponseEntity<?> disableTwoFactor(@AuthenticationPrincipal User user) {
        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecret(null);
        userRepository.save(user);

        // Remove códigos de backup
        backupCodeService.deleteAllBackupCodes(user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Autenticação em dois fatores desativada e códigos de backup removidos."
        ));
    }


    @PostMapping("/validate-login")
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
    public ResponseEntity<?> validateLogin2FA(@RequestBody @Valid TwoFactorLoginRequest request,
                                              HttpServletRequest httpRequest,
                                              HttpServletResponse response) {

        Pending2FALogin pending = pending2FALoginRepository.findByTempToken(request.getTempToken())
                .orElseThrow(() -> new Invalid2FATokenException("Invalid or expired 2FA token."));

        if (pending.getExpiresAt().isBefore(LocalDateTime.now())) {
            pending2FALoginRepository.delete(pending);
            throw new Expired2FATokenException("The 2FA token has expired. Please login again.");
        }

        User user = pending.getUser();

        boolean validCode = twoFactorAuthService.verifyCode(user.getTwoFactorSecret(), request.getTwoFactorCode());

        // Se o TOTP falhar, tenta código de backup
        if (!validCode && !backupCodeService.validateBackupCode(user, request.getTwoFactorCode())) {
            throw new Invalid2FACodeException("The 2FA code is incorrect.");
        }

        LoginWithRefreshResponse loginResponse = authService.completeLogin(user, request.isRememberMe(), httpRequest, response);

        pending2FALoginRepository.delete(pending);

        return ResponseEntity.ok(loginResponse);
    }


    @GetMapping("/backup-codes")
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
    public ResponseEntity<List<BackupCodeResponse>> listBackupCodes(@AuthenticationPrincipal User user) {
        List<BackupCode> codes = backupCodeService.getBackupCodes(user);

        List<BackupCodeResponse> response = codes.stream()
                .map(code -> new BackupCodeResponse(
                        code.getId(),
                        code.getCode(),
                        code.isUsed(),
                        code.getCreatedAt(),
                        code.getUsedAt()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }


    @PostMapping("/backup-codes/generate")
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
    public ResponseEntity<List<String>> generateBackupCodes(@AuthenticationPrincipal User user) {
        List<String> codes = backupCodeService.generateBackupCodes(user, 10);
        return ResponseEntity.ok(codes);
    }

    @DeleteMapping("/backup-codes")
    @Operation(
            summary = "Remove todos os códigos de backup do usuário",
            description = "Deleta permanentemente todos os códigos de backup associados ao usuário autenticado.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Códigos de backup removidos com sucesso")
    })
    public ResponseEntity<?> deleteBackupCodes(@AuthenticationPrincipal User user) {
        backupCodeService.deleteAllBackupCodes(user);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Todos os códigos de backup foram removidos com sucesso."
        ));
    }
}
