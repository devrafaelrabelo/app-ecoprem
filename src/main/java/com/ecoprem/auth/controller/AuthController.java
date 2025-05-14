package com.ecoprem.auth.controller;

import com.ecoprem.auth.dto.*;
import com.ecoprem.auth.entity.User;
import com.ecoprem.auth.repository.RefreshTokenRepository;
import com.ecoprem.auth.util.JwtCookieUtil;
import com.ecoprem.auth.security.JwtTokenProvider;
import com.ecoprem.auth.service.AuthService;
import com.ecoprem.auth.service.RefreshTokenService;
import com.ecoprem.auth.service.RevokedTokenService;
import com.ecoprem.common.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
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

    @Autowired
    private JwtCookieUtil jwtCookieUtil;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return JWT + refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Account locked, suspended or 2FA required",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   HttpServletRequest servletRequest,
                                   HttpServletResponse response) {
        LoginWithRefreshResponse loginResponse = authService.login(request, servletRequest);
        jwtCookieUtil.setTokenCookie(response, loginResponse.getAccessToken());
        return ResponseEntity.ok().build(); // Token já enviado via cookie
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal User user,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {

        String token = jwtCookieUtil.extractTokenFromCookie(request);
        if (token == null) {
            return ResponseEntity.badRequest().body("No token provided.");
        }

        LocalDateTime expiresAt = jwtTokenProvider.getExpirationDateFromJWT(token);
        revokedTokenService.revokeToken(token, user, expiresAt);
        refreshTokenRepository.deleteByUserId(user.getId());
        jwtCookieUtil.clearTokenCookie(response);

        return ResponseEntity.ok("Logged out successfully. Token revoked.");
    }

}
