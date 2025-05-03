package com.ecoprem.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class RateLimitConfig {

    private final RateLimitProperties rateLimitProperties;

    @Bean
    public Cache<String, Integer> loginAttemptsPerIp() {
        return Caffeine.newBuilder()
                .expireAfterWrite(rateLimitProperties.getLogin().getTtlSeconds(), TimeUnit.SECONDS)
                .maximumSize(10_000)
                .build();
    }

    @Bean
    public Cache<String, Integer> loginAttemptsPerEmail() {
        return Caffeine.newBuilder()
                .expireAfterWrite(rateLimitProperties.getLogin().getTtlSeconds(), TimeUnit.SECONDS)
                .maximumSize(10_000)
                .build();
    }

    @Bean
    public Cache<String, Integer> refreshAttemptsPerIp() {
        return Caffeine.newBuilder()
                .expireAfterWrite(rateLimitProperties.getRefresh().getTtlSeconds(), TimeUnit.SECONDS)
                .maximumSize(10_000)
                .build();
    }
}
