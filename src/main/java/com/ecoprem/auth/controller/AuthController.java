package com.ecoprem.auth.controller;

import com.ecoprem.auth.config.AuthPathProperties;
import com.ecoprem.auth.config.AuthProperties;
import com.ecoprem.auth.dto.*;
import com.ecoprem.auth.service.LoginFinalizerService;
import com.ecoprem.user.service.UserService;
import com.ecoprem.entity.user.User;
import com.ecoprem.auth.exception.*;
import com.ecoprem.auth.util.JwtCookieUtil;
import com.ecoprem.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Autenticação", description = "Endpoints relacionados à autenticação, sessão e controle de usuários.")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final JwtCookieUtil jwtCookieUtil;
    private final AuthPathProperties authPathProperties;
    private final AuthProperties authProperties;
    private final AuthService authService;
    private final LoginFinalizerService loginFinalizerService;

    @GetMapping("/DevTest")
    public ResponseEntity<Map<String, Object>> getAuthConfig() {
        Map<String, Object> result = new HashMap<>();

        AuthProperties.Durations durations = authProperties.getCookiesDurations();
        AuthProperties.CookieProperties props = authProperties.getCookiesProperties();
        AuthProperties.CookieNames names = authProperties.getCookieNames();

        result.put("accessTokenMin", durations.getAccessTokenMin());
        result.put("refreshShortMin", durations.getRefreshShortMin());
        result.put("refreshLongMin", durations.getRefreshLongMin());
        result.put("twofaTokenShortMin", durations.getTwofaShortMin());

        result.put("secure", props.isSecure());
        result.put("httpOnly", props.isHttpOnly());
        result.put("sameSite", props.getSameSite());

        result.put("cookieNameAccess", names.getAccess());
        result.put("cookieNameRefresh", names.getRefresh());
        result.put("cookieName2FA", names.getTwofa());

        result.put("publicPaths", authPathProperties.getPublicPaths());

        return ResponseEntity.ok(result);
    }


    @Operation(
            summary = "Autenticar usuário",
            description = "Autentica o usuário com email e senha. Emite cookies HttpOnly com access token e refresh token. Se 2FA estiver ativado, retorna erro 403 com token temporário."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido (tokens emitidos via cookie)",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"success\": true, \"message\": \"Login successful. Access token issued.\"}"))),
            @ApiResponse(responseCode = "403", description = "2FA obrigatório ou conta bloqueada",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"success\": false, \"error\": \"Two-factor authentication is required.\"}"))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida (ex: e-mail mal formatado)",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   HttpServletRequest servletRequest,
                                   HttpServletResponse response) {
        try {
            LoginResult result = authService.login(request, servletRequest);
            LoginWithRefreshResponse loginResponse = loginFinalizerService.finalizeLogin(
                    result.user(),
                    request.isRememberMe(),
                    servletRequest,
                    response
            );
            return ResponseEntity.ok(loginResponse);

        } catch (TwoFactorRequiredException e) {
            Duration duration = Duration.ofMinutes(authProperties.getCookiesDurations().getTwofaShortMin());
            jwtCookieUtil.setTempTokenCookie(response, e.getTempToken(), duration);

            return ResponseEntity.status(206).body(Map.of(
                    "2fa_required", true,
                    "message", e.getMessage()
            ));
        }
    }

    @Operation(
            summary = "Finalizar sessão do usuário",
            description = """
            Revoga o token de acesso (se presente), remove cookies HttpOnly e encerra a sessão do usuário atual.
            Esta operação é segura mesmo que não haja tokens nos cookies — útil para forçar logout em qualquer situação.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
                {
                  "success": true,
                  "message": "Logout process executed. Tokens cleared if present."
                }
        """))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao encerrar sessão",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
                {
                  "success": false,
                  "error": "Erro inesperado ao encerrar a sessão."
                }
        """)))
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal User user,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        try {
            authService.logout(user, request, response);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Logout process executed. Tokens cleared if present."
            ));

        } catch (Exception e) {
            log.error("❌ Erro inesperado ao encerrar sessão: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "error", "Erro inesperado ao encerrar a sessão."
            ));
        }
    }

    @Deprecated
    @Operation(
            summary = "Renovar token de acesso",
            description = "Utiliza o refresh token do cookie HttpOnly para gerar um novo access token. Retorna status da renovação da sessão."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token renovado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
            {
              "success": true,
              "message": "Token renovado com sucesso."
            }
        """))),
            @ApiResponse(responseCode = "400", description = "Refresh token ausente",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
            {
              "success": false,
              "error": "Refresh token não encontrado no cookie."
            }
        """))),
            @ApiResponse(responseCode = "401", description = "Token expirado ou inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
            {
              "success": false,
              "error": "Refresh token expirado. Faça login novamente."
            }
        """))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao renovar token",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
            {
              "success": false,
              "error": "Erro interno ao renovar o token."
            }
        """)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {

        log.warn("⚠️ Endpoint /api/auth/validate está obsoleto. Use /api/auth/session.");
        return ResponseEntity.status(HttpStatus.GONE)
                .body(Map.of("valid", false, "error", "Este endpoint foi descontinuado. Use /api/auth/session."));

//        try {
//            authService.refreshToken(request, response);
//
//            return ResponseEntity.ok(Map.of(
//                    "success", true,
//                    "message", "Token renovado com sucesso."
//            ));
//
//        } catch (MissingTokenException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(Map.of("success", false, "error", e.getMessage()));
//
//        } catch (RefreshTokenExpiredException | InvalidTokenException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("success", false, "error", e.getMessage()));
//
//        } catch (Exception e) {
//            log.error("Erro interno ao renovar token: {}", e.getMessage(), e);
//            authService.clearAuthCookies(response);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("success", false, "error", "Erro interno ao renovar o token."));
//        }
    }

    @Deprecated
    @Operation(
            summary = "Validar token de acesso",
            description = "Valida o token de acesso presente no cookie HttpOnly. Retorna os dados básicos do usuário se válido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token válido",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
            {
              "valid": true,
              "userId": "abc-123",
              "email": "rafael@empresa.com",
              "role": "ADMIN"
            }
        """))),
            @ApiResponse(responseCode = "400", description = "Token ausente ou malformado",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
            {
              "valid": false,
              "error": "Token não encontrado no cookie."
            }
        """))),
            @ApiResponse(responseCode = "401", description = "Token inválido ou expirado",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
            {
              "valid": false,
              "error": "Token expirado."
            }
        """))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao validar token",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
            {
              "valid": false,
              "error": "Erro interno ao validar token."
            }
        """)))
    })
    @GetMapping("/validate")
    public ResponseEntity<?> validate(HttpServletRequest request, HttpServletResponse response) {
        log.warn("⚠️ Endpoint /api/auth/validate está obsoleto. Use /api/auth/session.");
        return ResponseEntity.status(HttpStatus.GONE)
                .body(Map.of("valid", false, "error", "Este endpoint foi descontinuado. Use /api/auth/session."));

//        try {
//            Map<String, Object> result = authService.validateAccessToken(request, response);
//            return ResponseEntity.ok(result);
//
//        } catch (MissingTokenException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(Map.of("valid", false, "error", e.getMessage()));
//
//        } catch (InvalidTokenException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("valid", false, "error", e.getMessage()));
//
//        } catch (Exception e) {
//            log.error("Erro interno ao validar token: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("valid", false, "error", "Erro interno ao validar token."));
//        }
    }


    @Operation(
            summary = "Validar ou renovar sessão do usuário",
            description = """
        Endpoint unificado para validação e renovação da sessão.
        - Se o access token ainda for válido, mantém a sessão ativa.
        - Se expirado, tenta automaticamente renovar com o refresh token.
        - Se falhar, limpa os cookies e exige novo login.
    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sessão válida ou renovada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Sessão inválida ou expirada",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
        {
          "valid": false,
          "error": "Sessão inválida. Faça login novamente."
        }
    """))),
            @ApiResponse(responseCode = "500", description = "Erro interno na validação ou renovação",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
        {
          "valid": false,
          "error": "Erro interno ao validar ou renovar a sessão."
        }
    """)))
    })
    @GetMapping("/session")
    public ResponseEntity<?> validateOrRefreshSession(HttpServletRequest request, HttpServletResponse response) {
        try {
            SessionUserResponse session = authService.validateOrRefreshSession(request, response);
            return ResponseEntity.ok(Map.of("valid", true, "data", session)); // retorna dados atualizados

        } catch (MissingTokenException e) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "error", e.getMessage()));

        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "error", e.getMessage()));

        } catch (Exception e) {
            log.error("❌ Internal error validating/refreshing session: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("valid", false, "error", "Internal server error during session validation."));
        }
    }

    @Operation(summary = "Registro de novo usuário", description = "Cria um novo usuário com validações de email, username, senha e papéis.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou usuário já existente"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
