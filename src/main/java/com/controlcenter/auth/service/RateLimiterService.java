package com.controlcenter.auth.service;

import com.controlcenter.auth.cache.AuthCacheRegistry;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private static final int MAX_ATTEMPTS = 5;

    private final AuthCacheRegistry cacheRegistry;

    public boolean checkAndIncrement(String key) {
        Cache<String, Integer> cache = cacheRegistry.getForgotPasswordAttempts();

        Integer count = cache.getIfPresent(key);
        if (count != null && count >= MAX_ATTEMPTS) {
            return false;
        }

        cache.asMap().merge(key, 1, Integer::sum);
        return true;
    }

    public int getAttempts(String key) {
        Cache<String, Integer> cache = cacheRegistry.getForgotPasswordAttempts();
        return cache.getIfPresent(key) == null ? 0 : cache.getIfPresent(key);
    }
}
