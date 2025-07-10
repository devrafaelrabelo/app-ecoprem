package com.ecoprem.auth.service;

import com.ecoprem.auth.exception.*;
import com.ecoprem.user.repository.UserRepository;
import com.ecoprem.entity.user.User;
import com.ecoprem.entity.security.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserValidationService {

    private final UserRepository userRepository;

    public void validateUserState(User user) {
        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("Você precisa verificar seu e-mail antes de fazer login.");
        }

        String status = Optional.ofNullable(user.getStatus())
                .map(UserStatus::getName)
                .map(String::toLowerCase)
                .orElse(null);

        if ("suspended".equals(status)) {
            throw new AccountSuspendedException("Sua conta foi suspensa. Entre em contato com o suporte.");
        }

        if ("deactivated".equals(status)) {
            throw new AccountNotActiveException("Sua conta está desativada. Entre em contato com o suporte.");
        }

        if (user.isAccountLocked()) {
            if (isUnlockTimeReached(user)) {
                unlockUser(user);
            } else {
                throw new AccountLockedException("Sua conta está temporariamente bloqueada. Tente novamente mais tarde.");
            }
        }
    }

    public boolean isUnlockTimeReached(User user) {
        return user.getAccountLockedAt() != null &&
                user.getAccountLockedAt().plusMinutes(15).isBefore(LocalDateTime.now());
    }

    public void unlockUser(User user) {
        user.setAccountLocked(false);
        user.setLoginAttempts(0);
        user.setAccountLockedAt(null);
        userRepository.save(user);
    }
}
