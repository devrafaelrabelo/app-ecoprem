package com.controlcenter.auth.service;

import com.controlcenter.auth.repository.PasswordResetTokenRepository;
import com.controlcenter.user.repository.UserRepository;
import com.controlcenter.entity.auth.PasswordResetToken;
import com.controlcenter.entity.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.controlcenter.auth.util.ValidationUtil.isStrongPassword;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String createToken(User user, int expirationMinutes) {
        PasswordResetToken token = new PasswordResetToken();
        token.setId(UUID.randomUUID());
        token.setToken(UUID.randomUUID().toString().replace("-", ""));
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(expirationMinutes));
        tokenRepository.save(token);
        return token.getToken();
    }

    public Optional<PasswordResetToken> validateToken(String token) {
        return tokenRepository.findByToken(token)
                .filter(t -> !t.isUsed() && t.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        if (resetToken.isUsed() || resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expirado ou já utilizado");
        }

        if (!isStrongPassword(newPassword)) {
            throw new IllegalArgumentException("A nova senha não atende aos requisitos de segurança.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordLastUpdated(LocalDateTime.now());
        user.setPasswordCompromised(false);
        user.setForcedLogoutAt(LocalDateTime.now());

        resetToken.setUsed(true);

        userRepository.save(user);
        tokenRepository.save(resetToken);

        log.info("🔐 Senha redefinida com sucesso para o usuário {} ({})", user.getUsername(), user.getEmail());
        log.warn("🔐 [AUDIT] Reset de senha executado via token para userId={}, token={}", user.getId(), token);
    }
}
