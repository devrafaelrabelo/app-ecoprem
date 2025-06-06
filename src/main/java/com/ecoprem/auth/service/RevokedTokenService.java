package com.ecoprem.auth.service;

import com.ecoprem.entity.RevokedToken;
import com.ecoprem.entity.User;
import com.ecoprem.auth.repository.RevokedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RevokedTokenService {

    private final RevokedTokenRepository revokedTokenRepository;

    public void revokeToken(String token, User user, LocalDateTime expiresAt) {
        RevokedToken revoked = new RevokedToken();
        revoked.setId(UUID.randomUUID());
        revoked.setToken(token);
        revoked.setUser(user);
        revoked.setExpiresAt(expiresAt);

        revokedTokenRepository.save(revoked);
    }

    public boolean isTokenRevoked(String token) {
        return revokedTokenRepository.findByToken(token).isPresent();
    }
}
