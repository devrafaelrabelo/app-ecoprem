package com.ecoprem.auth.service;

import com.ecoprem.auth.config.AuthProperties;
import com.ecoprem.auth.dto.*;
import com.ecoprem.auth.entity.*;
import com.ecoprem.auth.exception.*;
import com.ecoprem.auth.repository.*;
import com.ecoprem.auth.security.JwtTokenProvider;
import com.ecoprem.auth.util.JwtCookieUtil;
import com.ecoprem.auth.util.LoginMetadataExtractor;
import com.github.benmanes.caffeine.cache.Cache;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


import static com.ecoprem.auth.util.ValidationUtil.isStrongPassword;
import static com.ecoprem.auth.util.ValidationUtil.isValidEmail;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginHistoryRepository loginHistoryRepository;
    private final LoginMetadataExtractor metadataExtractor;
    private final ActivityLogService activityLogService;
    private final ActiveSessionService activeSessionService;
    private final Pending2FALoginRepository pending2FALoginRepository;
    private final MailService mailService;
    public static final Set<String> ALLOWED_REGISTRATION_ROLES = Set.of("CLIENT", "BASIC_USER");


    private final Cache<String, Integer> loginAttemptsPerIp;
    private final Cache<String, Integer> loginAttemptsPerEmail;
    private final Cache<String, Integer> refreshAttemptsPerIp;
    private static final int MAX_ATTEMPTS_PER_MINUTE = 10;
    private static final int MAX_REFRESH_ATTEMPTS_PER_MINUTE = 10;
    private static final int MAX_ATTEMPTS_PER_EMAIL_PER_MINUTE = 10;

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtCookieUtil jwtCookieUtil;

    @Autowired
    private AuthProperties authProperties;

    public LoginResult login(LoginRequest request, HttpServletRequest servletRequest) {
        String ipAddress = metadataExtractor.getClientIp(servletRequest);
        String userAgent = metadataExtractor.getUserAgent(servletRequest);

        User user = null;
        boolean success = false;
        String failureReason = null;

        try {
            // 1. Validação de formato de e-mail
            if (!isValidEmail(request.getEmail())) {
                throw new InvalidRequestException("Invalid email format.");
            }

            // 2. Limite por IP
            Integer ipAttempts = loginAttemptsPerIp.getIfPresent(ipAddress);
            ipAttempts = (ipAttempts == null ? 0 : ipAttempts) + 1;
            loginAttemptsPerIp.put(ipAddress, ipAttempts);
            if (ipAttempts > MAX_ATTEMPTS_PER_MINUTE) {
                throw new RateLimitExceededException("Too many login attempts from this IP. Please try again later.");
            }

            // 3. Limite por e-mail
            Integer emailAttempts = loginAttemptsPerEmail.getIfPresent(request.getEmail());
            emailAttempts = (emailAttempts == null ? 0 : emailAttempts) + 1;
            loginAttemptsPerEmail.put(request.getEmail(), emailAttempts);
            if (emailAttempts > MAX_ATTEMPTS_PER_EMAIL_PER_MINUTE) {
                throw new RateLimitExceededException("Too many login attempts for this account. Please try again later.");
            }

            // 4. Busca usuário
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
            if (userOpt.isEmpty()) {
                throw new InvalidCredentialsException("The email or password you entered is incorrect.");
            }
            user = userOpt.get();

            // 5. E-mail não verificado
            if (!user.isEmailVerified()) {
                failureReason = "Email not verified";
                throw new EmailNotVerifiedException("Please verify your email before logging in.");
            }

            // 6. Status da conta
            if (user.getUserStatus() != null) {
                String status = user.getUserStatus().getStatus().toLowerCase();
                switch (status) {
                    case "suspended" -> {
                        failureReason = "Account suspended";
                        throw new AccountSuspendedException("Your account has been suspended. Please contact support.");
                    }
                    case "deactivated" -> {
                        failureReason = "Account deactivated";
                        throw new AccountNotActiveException("Your account is deactivated. Please contact support.");
                    }
                }
            }

            // 7. Conta bloqueada
            if (user.isAccountLocked()) {
                if (user.getAccountLockedAt() != null &&
                        user.getAccountLockedAt().plusMinutes(15).isBefore(LocalDateTime.now())) {
                    // Libera após tempo
                    user.setAccountLocked(false);
                    user.setLoginAttempts(0);
                    user.setAccountLockedAt(null);
                    userRepository.save(user);
                } else {
                    failureReason = "Account locked";
                    throw new AccountLockedException("Your account is locked. Please try again later.");
                }
            }

            // 8. Verifica senha
            success = passwordEncoder.matches(request.getPassword(), user.getPassword());
            if (!success) {
                failureReason = "Invalid password";
                int attempts = user.getLoginAttempts() + 1;
                user.setLoginAttempts(attempts);

                // Bloqueia se exceder tentativas
                if (attempts >= 5 && !user.isAccountLocked()) {
                    user.setAccountLocked(true);
                    user.setAccountLockedAt(LocalDateTime.now());
                    mailService.sendAccountLockedEmail(user.getEmail(), user.getUsername());
                }

                userRepository.save(user);

                if (user.isAccountLocked()) {
                    failureReason = "Account locked after failed attempts";
                    throw new AccountLockedException("Your account is locked. Please try again later.");
                }

                throw new InvalidCredentialsException("The email or password you entered is incorrect.");
            }

            // 9. Login bem-sucedido
            user.setLoginAttempts(0);
            userRepository.save(user);

            // 10. Verifica 2FA
            if (user.isTwoFactorEnabled()) {
                Pending2FALogin pending = new Pending2FALogin();
                pending.setId(UUID.randomUUID());
                pending.setUser(user);
                pending.setTempToken(UUID.randomUUID().toString());
                pending.setCreatedAt(LocalDateTime.now());
                pending.setExpiresAt(LocalDateTime.now().plusMinutes(10));
                pending2FALoginRepository.save(pending);

                throw new TwoFactorRequiredException(
                        "Two-factor authentication is required.",
                        pending.getTempToken()
                );
            }
            String sessionId = UUID.randomUUID().toString();
            activeSessionService.createSession(user, sessionId, servletRequest);

            // 11. Gera token e cria sessão
            String token = jwtTokenProvider.generateToken(
                    user.getId(),
                    user.getEmail(),
                    user.getRole().getName(),
                    sessionId
            );



            activityLogService.logActivity(user, "Logged in successfully", servletRequest);

            return new LoginResult(
                    new LoginWithRefreshResponse(
                            token,
                            null,
                            user.getUsername(),
                            user.getFullName(),
                            user.isTwoFactorEnabled()
                    ),
                    user
            );

        } finally {
            if (user != null) {
                recordLoginAttempt(user, ipAddress, userAgent, success, failureReason);
            }
        }
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

    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("The email is already in use.");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("The username is already in use.");
        }

        if (!isStrongPassword(request.getPassword())) {
            throw new PasswordTooWeakException("Password must be at least 8 characters, include uppercase, lowercase letters and a number.");
        }

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + request.getRole()));

        if (!ALLOWED_REGISTRATION_ROLES.contains(request.getRole().toUpperCase())) {
            throw new InvalidRoleAssignmentException("You are not allowed to register this type of account.");
        }

        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setFullName(request.getFullName() != null
                ? request.getFullName()
                : request.getFirstName() + " " + request.getLastName());
        newUser.setSocialName(request.getSocialName());
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(role);
        newUser.setEmailVerified(false);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        userRepository.save(newUser);
    }

    public LoginWithRefreshResponse refreshToken(RefreshTokenRequest request, HttpServletRequest servletRequest, HttpServletResponse response) {
        String ipAddress = metadataExtractor.getClientIp(servletRequest);

        int refreshAttempts = refreshAttemptsPerIp.get(ipAddress, k -> 0) + 1;
        refreshAttemptsPerIp.put(ipAddress, refreshAttempts);
        if (refreshAttempts > MAX_REFRESH_ATTEMPTS_PER_MINUTE) {
            throw new RateLimitExceededException("Too many refresh attempts. Please try again later.");
        }

        // ✅ Valida refresh token
        RefreshToken token = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RefreshTokenExpiredException("Invalid refresh token. Please login again."));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenExpiredException("Refresh token expired. Please login again.");
        }

        User user = token.getUser();

        // 🔄 Refresh rotativo: remove os tokens anteriores
        refreshTokenRepository.deleteByUserId(user.getId());

        // ✅ Gera novo refresh token
        RefreshToken newRefresh = new RefreshToken();
        newRefresh.setId(UUID.randomUUID());
        newRefresh.setToken(UUID.randomUUID().toString());
        newRefresh.setUser(user);
        newRefresh.setCreatedAt(LocalDateTime.now());
        newRefresh.setExpiresAt(LocalDateTime.now().plusDays(30));
        refreshTokenRepository.save(newRefresh);

        // ✅ Extrai sessionId do access token (do cookie)
        String accessToken = jwtCookieUtil.extractTokenFromCookie(servletRequest);
        String sessionId = null;

        try {
            Claims claims = jwtTokenProvider.extractClaims(accessToken);
            sessionId = claims.get("sessionId", String.class);
        } catch (Exception e) {
            // ⚠️ Se falhar, cria nova sessão
            sessionId = UUID.randomUUID().toString();
            activeSessionService.createSession(user, sessionId, servletRequest);
        }

        // ✅ Gera novo access token com sessionId
        String newAccessToken = jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getName(),
                sessionId
        );

        // 🍪 Atualiza os cookies
        jwtCookieUtil.setTokenCookie(response, newAccessToken);
        jwtCookieUtil.setRefreshTokenCookie(response, newRefresh.getToken(), Duration.ofDays(30));

        activityLogService.logActivity(user, "Refreshed token (cookie-based)", servletRequest);

        return new LoginWithRefreshResponse(
                newAccessToken,
                newRefresh.getToken(),
                user.getUsername(),
                user.getFullName(),
                user.isTwoFactorEnabled()
        );
    }

    public LoginWithRefreshResponse completeLogin(User user, boolean rememberMe,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) {

        // 🆔 Cria e registra sessionId
        String sessionId = UUID.randomUUID().toString();
        activeSessionService.createSession(user, sessionId, request);

        // 🔐 Gera access token
        String accessToken = jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getName(),
                sessionId
        );

        jwtCookieUtil.setTokenCookie(response, accessToken);

        // 🔁 Gerar refresh token com base no rememberMe
        Duration duration = rememberMe
                ? Duration.ofMinutes(authProperties.getCookiesDurations().getRefreshLongMin())
                : Duration.ofMinutes(authProperties.getCookiesDurations().getRefreshShortMin());

        refreshTokenRepository.deleteByUserId(user.getId());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID());
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshToken.setExpiresAt(LocalDateTime.now().plus(duration));
        refreshTokenRepository.save(refreshToken);

        jwtCookieUtil.setRefreshTokenCookie(response, refreshToken.getToken(), duration);

        // 📋 Log de atividade
        activityLogService.logActivity(user, "Login realizado com rememberMe=" + rememberMe, request);

        return new LoginWithRefreshResponse(
                accessToken,
                refreshToken.getToken(),
                user.getUsername(),
                user.getFullName(),
                user.isTwoFactorEnabled()
        );
    }

}
