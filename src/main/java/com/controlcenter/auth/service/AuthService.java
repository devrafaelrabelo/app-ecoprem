package com.controlcenter.auth.service;

import com.controlcenter.auth.config.AuthProperties;
import com.controlcenter.auth.dto.*;
import com.controlcenter.auth.repository.*;
import com.controlcenter.auth.security.JwtTokenProvider;
import com.controlcenter.auth.util.JwtCookieUtil;
import com.controlcenter.auth.util.LoginMetadataExtractor;
import com.controlcenter.entity.auth.*;
import com.controlcenter.entity.security.Permission;
import com.controlcenter.entity.user.User;
import com.controlcenter.exceptions.exception.*;
import com.controlcenter.user.repository.UserRepository;
import com.controlcenter.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final Pending2FALoginRepository pending2FALoginRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final LoginMetadataExtractor metadataExtractor;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthProperties authProperties;
    private final JwtCookieUtil jwtCookieUtil;

    private final ActivityLogService activityLogService;
    private final RevokedTokenService revokedTokenService;
    private final LoginAttemptService loginAttemptService;
    private final UserValidationService userValidationService;
    private final SessionService sessionService;
    private final UserService userService;
    private final TokenService tokenService;

    public LoginResult login(LoginRequest request, HttpServletRequest servletRequest) {
        String ipAddress = metadataExtractor.getClientIp(servletRequest);
        String userAgent = metadataExtractor.getUserAgent(servletRequest);

        User user = null;
        boolean authenticated = false;
        String failureReason = null;

        try {
            loginAttemptService.checkRateLimits(ipAddress, request.getEmail());

            user = userRepository.findByEmailForLogin(request.getEmail())
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password."));

            userValidationService.validateUserState(user);

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                failureReason = "Invalid password";
                loginAttemptService.handleInvalidPassword(user);
                throw new InvalidCredentialsException("Invalid email or password.");
            }

            loginAttemptService.resetLoginAttempts(user);

            if (user.isTwoFactorEnabled()) {
                Pending2FALogin pending = createPending2FALogin(user);
                throw new TwoFactorRequiredException("2FA required.", pending.getTempToken());
            }

            authenticated = true;
            return new LoginResult(null, user);

        } finally {
            if (user != null) {
                recordLoginAttemptAsync(user, ipAddress, userAgent, authenticated, failureReason);
            }
        }
    }

    private void recordLoginAttemptAsync(User user, String ip, String ua, boolean success, String reason) {
        CompletableFuture.runAsync(() ->
                recordLoginAttempt(user, ip, ua, success, reason)
        );
    }


    public void logout(User user, HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = jwtCookieUtil.extractTokenFromCookie(request);
            String sessionId = sessionService.extractSessionIdFromRequest(request);

            if (token != null && !token.isBlank() && user != null) {
                LocalDateTime expiresAt = jwtTokenProvider.getExpirationDateFromJWT(token);
                revokedTokenService.revokeToken(token, user, expiresAt);
            }

            if (user != null) {
                refreshTokenRepository.deleteByUserIdAndSessionId(user.getId(), sessionId);
                sessionService.deleteSession(sessionId);

                activityLogService.logActivity(user, "Logout realizado", request);
                log.info("🔓 Logout para usuário {} (sessionId={})", user.getEmail(), sessionId != null ? sessionId : "indefinida");
            } else {
                log.warn("🔓 Logout anônimo executado (sem user no contexto) — sessionId={}", sessionId != null ? sessionId : "indefinida");
            }

        } catch (Exception e) {
            log.error("⚠️ Erro durante logout (forçando limpeza): {}", e.getMessage(), e);
        } finally {
            clearAuthCookies(response);
        }
    }

    public LoginWithRefreshResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String ipAddress = metadataExtractor.getClientIp(request);
            loginAttemptService.checkRefreshRateLimit(ipAddress);

            RefreshToken oldToken = resolveValidRefreshToken(request);
            UUID userId = oldToken.getUser().getId(); // Só acessa o ID, que não é lazy
            User user = userRepository.fetchUserWithRolesAndStatus(userId)
                    .orElseThrow(() -> new InvalidTokenException("Usuário não encontrado ao renovar a sessão."));


            String sessionId = resolveSessionId(request);
            Duration duration = getRefreshDuration(oldToken);

            RefreshToken newRefresh = tokenService.createAndStoreRefreshToken(user, sessionId, duration);
            refreshTokenRepository.save(newRefresh);

            String accessToken = issueTokensAndSetCookies(user, sessionId, newRefresh, duration, response);

            activityLogService.logActivity(user, "Refreshed token via cookie", request);

            return new LoginWithRefreshResponse(
                    accessToken,
                    newRefresh.getToken(),
                    user.getUsername(),
                    user.getFullName(),
                    user.isTwoFactorEnabled()
            );

        } catch (MissingTokenException | RefreshTokenExpiredException | RateLimitExceededException | InvalidTokenException e) {
            clearAuthCookies(response);
            throw e;

        } catch (Exception e) {
            log.error("❌ Erro inesperado ao renovar a sessão: {}", e.getMessage(), e);
            clearAuthCookies(response);
            throw new InvalidTokenException("Erro inesperado ao renovar a sessão.");
        }
    }

    public SessionUserResponse validateOrRefreshSession(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtCookieUtil.extractTokenFromCookie(request);

        if (accessToken != null) {
            try {
                Claims claims = jwtTokenProvider.extractClaims(accessToken);

                if (!jwtTokenProvider.isTokenExpired(claims)) {
                    String email = claims.get("email", String.class);
                    User user = userRepository.findByEmailWithStatusAndRoles(email)
                            .orElseThrow(() -> new InvalidTokenException("Usuário não encontrado (validateOrRefreshSession)."));
                    return userService.toSessionUserResponse(user);
                }

                log.info("🔁 Token de acesso expirado. Tentando usar refresh...");

            } catch (ExpiredJwtException e) {
                log.info("🔁 Access token realmente expirado.");
            } catch (JwtException e) {
                log.warn("❌ Access token inválido.");
                clearAuthCookies(response);
                throw new InvalidTokenException("Token inválido.");
            }
        }

        try {
            LoginWithRefreshResponse refreshResponse = refreshToken(request, response);
            User user = userRepository.fetchUserWithRolesAndStatusUsername(refreshResponse.getUsername())
                    .orElseThrow(() -> new InvalidTokenException("Usuário não encontrado após refresh."));
            return userService.toSessionUserResponse(user);

        } catch (Exception e) {
            log.warn("❌ Falha ao renovar sessão: 201 : {}", e.getMessage());
            clearAuthCookies(response);
            throw new InvalidTokenException("Sessão inválida. Faça login novamente.");
        }
    }


    private Pending2FALogin createPending2FALogin(User user) {
        Pending2FALogin pending = new Pending2FALogin();
        pending.setId(UUID.randomUUID());
        pending.setUser(user);
        pending.setTempToken(UUID.randomUUID().toString());
        pending.setCreatedAt(LocalDateTime.now());
        pending.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        pending2FALoginRepository.save(pending);
        return pending;
    }

    private void recordLoginAttempt(User user, String ipAddress, String userAgent, boolean success, String reason) {
        LoginHistory history = new LoginHistory();
        history.setId(UUID.randomUUID());
        history.setUser(user);
        history.setLoginDate(LocalDateTime.now());
        history.setIpAddress(ipAddress);
        history.setLocation(metadataExtractor.getLocation(ipAddress));
        history.setDevice(metadataExtractor.detectDevice(userAgent));
        history.setBrowser(metadataExtractor.detectBrowser(userAgent));
        history.setOperatingSystem(metadataExtractor.detectOS(userAgent));
        history.setSuccess(success);
        history.setFailureReason(reason);
        loginHistoryRepository.save(history);
    }

    public void clearAuthCookies(HttpServletResponse response) {
        jwtCookieUtil.clearTokenCookie(response);
        jwtCookieUtil.clearRefreshTokenCookie(response);
        jwtCookieUtil.clearTempTokenCookie(response);
    }

    private RefreshToken resolveValidRefreshToken(HttpServletRequest request) {
        String refreshTokenValue = jwtCookieUtil.extractRefreshTokenFromCookie(request);
        String sessionId = resolveSessionId(request);

        if (refreshTokenValue == null || sessionId == null || sessionId.isBlank()) {
            throw new InvalidTokenException("Refresh token ou sessionId ausente.");
        }

        RefreshToken token = refreshTokenRepository
                .findByTokenAndSessionId(refreshTokenValue, sessionId)
                .orElseThrow(() -> new RefreshTokenExpiredException("Refresh token não encontrado para esta sessão."));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenExpiredException("Refresh token expirado. Faça login novamente.");
        }

        return token;
    }

    private String resolveSessionId(HttpServletRequest request) {
        String accessToken = jwtCookieUtil.extractTokenFromCookie(request);
        if (accessToken != null) {
            try {
                Claims claims = jwtTokenProvider.extractClaims(accessToken);
                return claims.get("sessionId", String.class);
            } catch (Exception e) {
                log.warn("❌ Erro ao extrair sessionId do access token: {}", e.getMessage());
            }
        } else {
            log.warn("⚠️ Access token não encontrado nos cookies.");
        }

        String refreshToken = jwtCookieUtil.extractRefreshTokenFromCookie(request);
        if (refreshToken != null) {
            Optional<RefreshToken> token = refreshTokenRepository.findByToken(refreshToken);
            if (token.isPresent()) {
                return token.get().getSessionId();
            } else {
                log.warn("⚠️ Refresh token recebido, mas não encontrado no banco: {}", refreshToken);
            }
        } else {
            log.warn("⚠️ Refresh token não encontrado nos cookies.");
        }
        throw new InvalidTokenException("Não foi possível determinar o sessionId.");
    }

    private UUID resolveUserIdFromToken(HttpServletRequest request) {
        String accessToken = jwtCookieUtil.extractTokenFromCookie(request);
        if (accessToken == null) {
            throw new MissingTokenException("Access token ausente para extrair o usuário.");
        }

        try {
            Claims claims = jwtTokenProvider.extractClaims(accessToken);
            return UUID.fromString(claims.getSubject()); // subject = userId
        } catch (Exception e) {
            throw new InvalidTokenException("Token inválido ao tentar extrair userId.");
        }
    }

    private Duration getRefreshDuration(RefreshToken oldToken) {
        try {
            if (oldToken.getCreatedAt() == null || oldToken.getExpiresAt() == null) {
                log.warn("⚠️ RefreshToken com datas nulas. Aplicando fallback padrão (short).");
                return Duration.ofMinutes(authProperties.getCookiesDurations().getRefreshShortMin());
            }

            Duration existingDuration = Duration.between(oldToken.getCreatedAt(), oldToken.getExpiresAt());

            return existingDuration.toHours() >= 24
                    ? Duration.ofMinutes(authProperties.getCookiesDurations().getRefreshLongMin())
                    : Duration.ofMinutes(authProperties.getCookiesDurations().getRefreshShortMin());

        } catch (Exception e) {
            log.error("❌ Erro ao calcular duração do refresh token. Aplicando fallback padrão (short).", e);
            return Duration.ofMinutes(authProperties.getCookiesDurations().getRefreshShortMin());
        }
    }

    private String issueTokensAndSetCookies(User user, String sessionId,
                                            RefreshToken refreshToken, Duration duration,
                                            HttpServletResponse response) {

        // Gera lista de permissões do usuário
        List<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .distinct()
                .toList();

        // Gera o token com base nas permissões
        String accessToken = jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                permissions,
                sessionId
        );

        jwtCookieUtil.setTokenCookie(response, accessToken);
        jwtCookieUtil.setRefreshTokenCookie(response, refreshToken.getToken(), duration);

        return accessToken;
    }


    private String extractAccessTokenOrThrow(HttpServletRequest request) {
        String token = jwtCookieUtil.extractTokenFromCookie(request);
        if (token == null || token.isBlank()) {
            throw new MissingTokenException("Token não encontrado no cookie.");
        }
        return token;
    }

    private Map<String, Object> buildClaimsResponse(Claims claims) {
        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("userId", claims.getSubject());
        response.put("email", claims.get("email"));        // pode ser null
        response.put("role", claims.get("role"));          // pode ser null
        response.put("expiresAt", claims.getExpiration()); // pode ser null
        return response;
    }


}
