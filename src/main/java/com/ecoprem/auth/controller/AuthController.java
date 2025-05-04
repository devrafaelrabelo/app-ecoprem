package com.ecoprem.auth.controller;

import com.ecoprem.auth.dto.*;
import com.ecoprem.auth.entity.User;
import com.ecoprem.auth.repository.RefreshTokenRepository;
import com.ecoprem.auth.security.JwtTokenProvider;
import com.ecoprem.auth.service.AuthService;
import com.ecoprem.auth.service.RefreshTokenService;
import com.ecoprem.auth.service.RevokedTokenService;
import com.ecoprem.common.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private RefreshTokenRepository refreshTokenRepository;
    private RefreshTokenService refreshTokenService;
    private JwtTokenProvider jwtTokenProvider;
    private RevokedTokenService revokedTokenService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return JWT + refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login",
                    content = @Content(schema = @Schema(implementation = LoginWithRefreshResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request (e.g., malformed JSON or missing fields)",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Account locked, suspended or 2FA required",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        LoginWithRefreshResponse response = authService.login(request, servletRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal User user,
                                    HttpServletRequest request) {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("No token provided.");
        }

        String token = header.substring(7); // Remove "Bearer "

        // 🔥 Revogar o token atual (pega expiração do JWT)
        LocalDateTime expiresAt = jwtTokenProvider.getExpirationDateFromJWT(token);
        revokedTokenService.revokeToken(token, user, expiresAt);

        //  Também opcionalmente remova refresh token
        refreshTokenRepository.deleteByUserId(user.getId());

        return ResponseEntity.ok("Logged out successfully. Token revoked.");
    }

}
