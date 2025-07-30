package com.controlcenter.auth.cache;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Registro centralizado de caches utilizados no módulo de autenticação do ControlCenter.
 *
 * <p>Este componente organiza e disponibiliza instâncias nomeadas de {@link com.github.benmanes.caffeine.cache.Cache}
 * com finalidade específica em segurança, autenticação e controle de tentativas. O uso deste registry evita conflitos
 * de injeção de tipos genéricos e melhora a coesão entre os serviços.</p>
 *
 * <h2>Caches disponíveis</h2>
 * <ul>
 *   <li><b>loginAttemptsPerIp</b> — Tentativas de login por IP (1 min)</li>
 *   <li><b>loginAttemptsPerEmail</b> — Tentativas de login por e-mail (1 min)</li>
 *   <li><b>refreshAttemptsPerIp</b> — Tentativas de refresh token por IP (1 min)</li>
 *   <li><b>forgotPasswordAttempts</b> — Tentativas de recuperação de senha por e-mail (15 min)</li>
 *   <li><b>twoFactorAttemptsPerUser</b> — Tentativas incorretas de código 2FA por usuário (10 min)</li>
 * </ul>
 *
 * <h2>Serviços que utilizam este registro</h2>
 * <ul>
 *   <li>{@link com.controlcenter.auth.service.LoginAttemptService}</li>
 *   <li>{@link com.controlcenter.auth.service.TwoFactorAuthService}</li>
 *   <li>{@link com.controlcenter.auth.service.RateLimiterService}</li>
 * </ul>
 *
 * <p>Este padrão permite futura substituição dos caches para Redis ou outro backend com mínima alteração no código.</p>
 *
 * @author Rafael Rabelo
 * @since 2025-06
 */

@Data
@Component
@RequiredArgsConstructor
public class AuthCacheRegistry {

    private final Cache<String, Integer> loginAttemptsPerIp;
    private final Cache<String, Integer> loginAttemptsPerEmail;
    private final Cache<String, Integer> refreshAttemptsPerIp;
    private final Cache<String, Integer> forgotPasswordAttempts;
    private final Cache<UUID, Integer> twoFactorAttemptsPerUser;
}