package com.ecoprem.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class RateLimitConfig {

    @Bean
    public Cache<String, Integer> loginAttemptsPerIp() {
        return Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(10_000)
                .build();
    }

    @Bean
    public Cache<String, Integer> loginAttemptsPerEmail() {
        return Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(10_000)
                .build();
    }

    @Bean
    public Cache<String, Integer> refreshAttemptsPerIp() {
        return Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(10_000)
                .build();
    }

}
