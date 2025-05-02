package com.ecoprem.auth.controller;

import com.ecoprem.auth.dto.*;
import com.ecoprem.auth.entity.RefreshToken;
import com.ecoprem.auth.entity.User;
import com.ecoprem.auth.exception.InvalidRequestException;
import com.ecoprem.auth.repository.RefreshTokenRepository;
import com.ecoprem.auth.security.JwtTokenProvider;
import com.ecoprem.auth.service.AuthService;
import com.ecoprem.auth.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private RefreshTokenRepository refreshTokenRepository;
    private RefreshTokenService refreshTokenService;
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        LoginWithRefreshResponse response = authService.login(request, servletRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidRequestException("Invalid refresh token."));

        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();

        String newAccessToken = jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getName()
        );

        return ResponseEntity.ok(new LoginWithRefreshResponse(
                newAccessToken,
                request.getRefreshToken(),  // reuse same refresh token
                user.getUsername(),
                user.getFullName(),
                user.isTwoFactorEnabled()
        ));
    }
}
