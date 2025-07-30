package com.controlcenter.auth.service;

import com.controlcenter.entity.auth.RevokedToken;
import com.controlcenter.entity.user.User;
import com.controlcenter.auth.repository.RevokedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RevokedTokenService {

    private final RevokedTokenRepository revokedTokenRepository;

    public void revokeToken(String token, User user, LocalDateTime expiresAt) {
        RevokedToken revokedToken = new RevokedToken();
        revokedToken.setToken(token);
        revokedToken.setUser(user);
        revokedToken.setRevokedAt(LocalDateTime.now());
        revokedToken.setExpiresAt(expiresAt);
        revokedTokenRepository.save(revokedToken);
    }

    public boolean isTokenRevoked(String token) {
        return revokedTokenRepository.existsByToken(token);
    }

    public void deleteExpiredTokens() {
        revokedTokenRepository.deleteAllByExpiresAtBefore(LocalDateTime.now());
    }
}
