package com.controlcenter.auth.service;

import com.controlcenter.auth.repository.RefreshTokenRepository;
import com.controlcenter.entity.auth.RefreshToken;
import com.controlcenter.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TokenService {

   @Autowired
   private RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createAndStoreRefreshToken(User user, String sessionId, Duration duration)  {
        // Apenas remove tokens antigos **dessa sessão**
        refreshTokenRepository.deleteByUserIdAndSessionId(user.getId(), sessionId);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID());
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setSessionId(sessionId);
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshToken.setExpiresAt(LocalDateTime.now().plus(duration));
        refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }
}
