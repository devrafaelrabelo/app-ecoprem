package com.ecoprem.auth.service;

import com.ecoprem.entity.RefreshToken;
import com.ecoprem.entity.User;
import com.ecoprem.auth.exception.RefreshTokenExpiredException;
import com.ecoprem.auth.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Cria um novo refresh token e invalida o(s) anterior(es) para o usuário.
     *
     * @param user       Usuário dono do token
     * @param daysValid  Quantidade de dias que o token será válido (ex: 30 para remember-me)
     * @return           O refresh token criado
     */
    @Transactional
    public RefreshToken createRefreshToken(User user, int daysValid) {
        // 🔄 Remove tokens antigos para este usuário (refresh rotativo)
        refreshTokenRepository.deleteByUserId(user.getId());
        refreshTokenRepository.flush(); // Adicione esta linha!

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID());
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(daysValid));  // ✅ usando o parâmetro corretamente
        refreshToken.setCreatedAt(LocalDateTime.now());

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Verifica se o token ainda está válido (não expirou).
     * Se estiver expirado, já remove e lança exception.
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenExpiredException("Refresh token expired. Please login again.");
        }
        return token;
    }
}
