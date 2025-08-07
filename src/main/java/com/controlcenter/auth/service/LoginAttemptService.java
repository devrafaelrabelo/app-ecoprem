package com.controlcenter.auth.service;

import com.controlcenter.auth.cache.AuthCacheRegistry;
import com.controlcenter.exceptions.exception.AccountLockedException;
import com.controlcenter.exceptions.exception.RateLimitExceededException;
import com.controlcenter.user.repository.UserRepository;
import com.controlcenter.entity.user.User;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS_PER_MINUTE_IP = 10;
    private static final int MAX_ATTEMPTS_PER_MINUTE_EMAIL = 10;
    private static final int MAX_USER_LOGIN_ATTEMPTS = 5;
    private static final int MAX_REFRESH_ATTEMPTS_PER_MINUTE = 10;

    private final AuthCacheRegistry cacheRegistry;
    private final UserRepository userRepository;
    private final MailService mailService;

    public void checkRateLimits(String ipAddress, String email) {
        int ipAttempts = incrementAndGet(cacheRegistry.getLoginAttemptsPerIp(), ipAddress);
        if (ipAttempts > MAX_ATTEMPTS_PER_MINUTE_IP) {
            throw new RateLimitExceededException("Muitas tentativas de login a partir deste IP. Tente novamente mais tarde.");
        }

        int emailAttempts = incrementAndGet(cacheRegistry.getLoginAttemptsPerEmail(), email);
        if (emailAttempts > MAX_ATTEMPTS_PER_MINUTE_EMAIL) {
            throw new RateLimitExceededException("Muitas tentativas de login para esta conta. Tente novamente mais tarde.");
        }
    }

    public void checkRefreshRateLimit(String ipAddress) {
        Cache<String, Integer> refreshCache = cacheRegistry.getRefreshAttemptsPerIp();
        int attempts = refreshCache.get(ipAddress, k -> 0) + 1;
        refreshCache.put(ipAddress, attempts);

        if (attempts > MAX_REFRESH_ATTEMPTS_PER_MINUTE) {
            throw new RateLimitExceededException("Muitas tentativas de refresh. Tente novamente em instantes.");
        }
    }

    public void handleInvalidPassword(User user) {
        int attempts = user.getLoginAttempts() + 1;
        user.setLoginAttempts(attempts);

        if (attempts >= MAX_USER_LOGIN_ATTEMPTS && !user.isAccountLocked()) {
            user.setAccountLocked(true);
            user.setAccountLockedAt(LocalDateTime.now());
            mailService.sendAccountLockedEmail(user.getEmail(), user.getUsername());
        }

        userRepository.save(user);

        if (user.isAccountLocked()) {
            throw new AccountLockedException("Sua conta está temporariamente bloqueada. Tente novamente mais tarde.");
        }
    }

    public void resetLoginAttempts(User user) {
        user.setLoginAttempts(0);
        userRepository.save(user);
    }

    private int incrementAndGet(Cache<String, Integer> cache, String key) {
        int attempts = cache.getIfPresent(key) == null ? 0 : cache.getIfPresent(key);
        attempts++;
        cache.put(key, attempts);
        return attempts;
    }

    public void resetRateLimiting(String ipAddress, String email) {
        if (ipAddress != null && !ipAddress.isBlank()) {
            cacheRegistry.getLoginAttemptsPerIp().invalidate(ipAddress);
            cacheRegistry.getRefreshAttemptsPerIp().invalidate(ipAddress);
        }

        if (email != null && !email.isBlank()) {
            cacheRegistry.getLoginAttemptsPerEmail().invalidate(email);
        }

        log.debug("🔄 Rate limiting resetado para IP: {} e Email: {}", ipAddress, email);
    }
}
