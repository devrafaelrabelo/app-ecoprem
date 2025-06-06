package com.ecoprem.auth.controller;

import com.ecoprem.auth.config.AuthProperties;
import com.ecoprem.auth.dto.*;
import com.ecoprem.entity.User;
import com.ecoprem.auth.exception.*;
import com.ecoprem.auth.util.JwtCookieUtil;
import com.ecoprem.auth.security.JwtTokenProvider;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Autenticação", description = "Endpoints relacionados à autenticação, sessão e controle de usuários.")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JwtCookieUtil jwtCookieUtil;

    @Autowired
    private AuthProperties authProperties;

    @GetMapping("/DevTest")
    public ResponseEntity<Map<String, Object>> getAuthConfig() {
        Map<String, Object> result = new HashMap<>();

        AuthProperties.Durations durations = authProperties.getCookiesDurations();
        AuthProperties.CookieProperties props = authProperties.getCookiesProperties();
        AuthProperties.CookieNames names = authProperties.getCookieNames();

        result.put("accessTokenMin", durations.getAccessTokenMin());
        result.put("refreshShortMin", durations.getRefreshShortMin());
        result.put("refreshLongMin", durations.getRefreshLongMin());

        result.put("secure", props.isSecure());
        result.put("httpOnly", props.isHttpOnly());
        result.put("sameSite", props.getSameSite());

        result.put("cookieNameAccess", names.getAccess());
        result.put("cookieNameRefresh", names.getRefresh());

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
        log.info("Login received. rememberMe = {}", request.isRememberMe());

        LoginResult result = authService.login(request, servletRequest);

        LoginWithRefreshResponse loginResponse = authService.completeLogin(
                result.user(),
                request.isRememberMe(),
                servletRequest,
                response
        );

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Login successful. Access token issued."
        ));
    }

    @Operation(
            summary = "Finalizar sessão do usuário",
            description = "Revoga o token de acesso, remove cookies HttpOnly e encerra a sessão do usuário atual."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"success\": true, \"message\": \"Logged out successfully. Token revoked.\"}"))),
            @ApiResponse(responseCode = "400", description = "Token não encontrado no cookie",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"success\": false, \"error\": \"No token provided.\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao encerrar sessão",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal User user,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        String token = jwtCookieUtil.extractTokenFromCookie(request);
        if (token == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "No token provided."
            ));
        }

        authService.logout(user, token, request, response);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Logged out successfully. Token revoked."
        ));
    }


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
        try {
            authService.refreshToken(request, response);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Token renovado com sucesso."
            ));

        } catch (MissingTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", e.getMessage()));

        } catch (RefreshTokenExpiredException | InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "error", e.getMessage()));

        } catch (Exception e) {
            log.error("Erro interno ao renovar token: {}", e.getMessage(), e);
            authService.clearAuthCookies(response);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", "Erro interno ao renovar o token."));
        }
    }

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
        try {
            Map<String, Object> result = authService.validateAccessToken(request, response);
            return ResponseEntity.ok(result);

        } catch (MissingTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("valid", false, "error", e.getMessage()));

        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "error", e.getMessage()));

        } catch (Exception e) {
            log.error("Erro interno ao validar token: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("valid", false, "error", "Erro interno ao validar token."));
        }
    }


    @Operation(
            summary = "Validar ou renovar sessão",
            description = "Valida o token de acesso presente no cookie. Se estiver expirado, tenta usar o refresh token para renovar a sessão. Retorna dados básicos do usuário."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessão válida ou renovada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
            {
              "valid": true,
              "userId": "abc-123",
              "email": "rafael@empresa.com",
              "role": "ADMIN"
            }
        """))),
            @ApiResponse(responseCode = "400", description = "Token ausente",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
            {
              "valid": false,
              "error": "Token não encontrado no cookie."
            }
        """))),
            @ApiResponse(responseCode = "401", description = "Tokens inválidos ou expirados",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
            {
              "valid": false,
              "error": "Sessão inválida. Faça login novamente."
            }
        """))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao renovar ou validar sessão",
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
            Map<String, Object> result = authService.validateOrRefreshSession(request, response);
            return ResponseEntity.ok(result);

        } catch (MissingTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("valid", false, "error", e.getMessage()));

        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "error", e.getMessage()));

        } catch (Exception e) {
            log.error("Erro interno ao validar/renovar sessão: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("valid", false, "error", "Erro interno ao validar ou renovar a sessão."));
        }
    }


    @Operation(
            summary = "Obter dados do usuário autenticado",
            description = "Retorna os dados do usuário autenticado com base no token de sessão."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileDTO.class))),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal User user) {
        try {
            UserProfileDTO profile = authService.getCurrentUserProfile(user);
            return ResponseEntity.ok(Map.of("success", true, "data", profile));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
