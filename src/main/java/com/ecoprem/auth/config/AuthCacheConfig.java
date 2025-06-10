package com.ecoprem.auth.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.UUID;

/**
 * Configuração dos caches utilizados no módulo de autenticação.
 *
 * Utiliza Caffeine Cache para controle de tentativas, performance e rate limiting.
 */
@Configuration
public class AuthCacheConfig {

    /**
     * Cache que armazena o número de tentativas de autenticação 2FA por usuário.
     * Expira após 10 minutos e tem capacidade máxima de 1000 entradas.
     */
    @Bean
    public Cache<UUID, Integer> twoFactorAttemptsPerUser() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(10))
                .maximumSize(1000)
                .build();
    }

    // No futuro, outros caches podem ser adicionados aqui,
    // como tentativas por IP, blacklist de tokens, etc.
}
