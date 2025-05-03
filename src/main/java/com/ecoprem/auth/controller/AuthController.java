package com.ecoprem.auth.controller;

import com.ecoprem.auth.dto.*;
import com.ecoprem.auth.entity.RefreshToken;
import com.ecoprem.auth.entity.User;
import com.ecoprem.auth.exception.InvalidRequestException;
import com.ecoprem.auth.exception.InvalidRoleAssignmentException;
import com.ecoprem.auth.repository.RefreshTokenRepository;
import com.ecoprem.auth.security.JwtTokenProvider;
import com.ecoprem.auth.service.AuthService;
import com.ecoprem.auth.service.RefreshTokenService;
import com.ecoprem.auth.service.RevokedTokenService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/login")
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
