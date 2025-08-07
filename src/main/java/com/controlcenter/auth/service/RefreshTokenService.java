package com.controlcenter.auth.service;

import com.controlcenter.auth.repository.RefreshTokenRepository;
import com.controlcenter.entity.auth.RefreshToken;
import com.controlcenter.entity.user.User;
import com.controlcenter.exceptions.exception.RefreshTokenExpiredException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Cria e salva um novo RefreshToken com duração personalizada para uma sessão específica.
     */
    public RefreshToken createRefreshToken(User user, String sessionId, Duration duration) {
        RefreshToken token = new RefreshToken();
        token.setId(UUID.randomUUID());
        token.setUser(user);
        token.setSessionId(sessionId);
        token.setToken(UUID.randomUUID().toString());
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plus(duration));
        return refreshTokenRepository.save(token);
    }

    /**
     * Remove todos os tokens de refresh associados ao usuário.
     */
    @Transactional
    public void deleteByUserId(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    /**
     * Remove o token de refresh associado à sessão do usuário.
     */
    @Transactional
    public void deleteByUserIdAndSessionId(UUID userId, String sessionId) {
        refreshTokenRepository.deleteByUserIdAndSessionId(userId, sessionId);
    }

    /**
     * Busca um token válido por usuário e sessão. Lança exceção se estiver expirado.
     */
    public RefreshToken findValidToken(UUID userId, String sessionId) {
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByUserIdAndSessionId(userId, sessionId);

        RefreshToken token = tokenOpt.orElseThrow(() ->
                new RefreshTokenExpiredException("Refresh token não encontrado para esta sessão."));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenExpiredException("Refresh token expirado. Faça login novamente.");
        }

        return token;
    }
}
