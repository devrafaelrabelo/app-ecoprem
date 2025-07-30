package com.controlcenter.auth.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Configuração dos caches utilizados no módulo de autenticação.
 *
 * Utiliza Caffeine Cache para controle de tentativas, performance e rate limiting.
 */
@Configuration
public class AuthCacheConfig {

    /**
     * Tentativas de 2FA por usuário (UUID).
     */
    @Bean
    public Cache<UUID, Integer> twoFactorAttemptsPerUser() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(10))
                .maximumSize(1000)
                .build();
    }

    /**
     * Tentativas de login por IP.
     */
    @Bean
    public Cache<String, Integer> loginAttemptsPerIp() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(1))
                .maximumSize(1000)
                .build();
    }

    /**
     * Tentativas de login por e-mail.
     */
    @Bean
    public Cache<String, Integer> loginAttemptsPerEmail() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(1))
                .maximumSize(1000)
                .build();
    }

    /**
     * Tentativas de refresh por IP.
     */
    @Bean
    public Cache<String, Integer> refreshAttemptsPerIp() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(1))
                .maximumSize(1000)
                .build();
    }

    /**
     * Tentativas de reset de senha por e-mail.
     */
    @Bean
    public Cache<String, Integer> forgotPasswordAttempts() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(15))
                .maximumSize(1000)
                .build();
    }

    /**
     * Cache manager para uso com @Cacheable (se necessário futuramente).
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("forgotPasswordAttempts");
        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(1000));
        return manager;
    }
}
