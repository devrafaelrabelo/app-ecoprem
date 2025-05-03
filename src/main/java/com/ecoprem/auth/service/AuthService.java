package com.ecoprem.auth.service;

import com.ecoprem.auth.dto.*;
import com.ecoprem.auth.entity.*;
import com.ecoprem.auth.exception.*;
import com.ecoprem.auth.repository.*;
import com.ecoprem.auth.security.JwtTokenProvider;
import com.ecoprem.auth.util.LoginMetadataExtractor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
    private final RefreshTokenService refreshTokenService;

    private final Map<String, Integer> loginAttemptsPerIp = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS_PER_MINUTE = 10;

    private final Map<String, Integer> refreshAttemptsPerIp = new ConcurrentHashMap<>();
    private static final int MAX_REFRESH_ATTEMPTS_PER_MINUTE = 20;

    private final Map<String, Integer> loginAttemptsPerEmail = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS_PER_EMAIL_PER_MINUTE = 10;

    private RefreshTokenRepository refreshTokenRepository;

    public LoginWithRefreshResponse login(LoginRequest request, HttpServletRequest servletRequest) {
        String ipAddress = metadataExtractor.getClientIp(servletRequest);
        String userAgent = metadataExtractor.getUserAgent(servletRequest);

        User user = null;
        boolean success = false;
        String failureReason = null;

        try {
            if (!isValidEmail(request.getEmail())) {
                throw new InvalidRequestException("Invalid email format.");
            }

            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

            // Controle de tentativas por IP
            loginAttemptsPerIp.merge(ipAddress, 1, Integer::sum);
            if (loginAttemptsPerIp.get(ipAddress) > MAX_ATTEMPTS_PER_MINUTE) {
                throw new RateLimitExceededException("Too many login attempts. Please try again later.");
            }

            // ✅ Rate limit por email (adicional ao IP)
            loginAttemptsPerEmail.merge(request.getEmail(), 1, Integer::sum);
            if (loginAttemptsPerEmail.get(request.getEmail()) > MAX_ATTEMPTS_PER_EMAIL_PER_MINUTE) {
                throw new RateLimitExceededException("Too many login attempts for this account. Please try again later.");
            }

            if (userOpt.isEmpty()) {
                throw new InvalidCredentialsException("The email or password you entered is incorrect.");
            }

            user = userOpt.get();

            if (!user.isEmailVerified()) {
                failureReason = "Email not verified";
                throw new EmailNotVerifiedException("Please verify your email before logging in.");
            }

            if (user.getUserStatus() != null) {
                String status = user.getUserStatus().getStatus().toLowerCase();
                if ("suspended".equals(status)) {
                    failureReason = "Account suspended";
                    throw new AccountSuspendedException("Your account has been suspended. Please contact support.");
                } else if ("deactivated".equals(status)) {
                    failureReason = "Account deactivated";
                    throw new AccountNotActiveException("Your account is deactivated. Please contact support.");
                }
            }

            if (user.isAccountLocked()) {
                if (user.getAccountLockedAt() != null &&
                        user.getAccountLockedAt().plusMinutes(15).isBefore(LocalDateTime.now())) {
                    // Desbloqueia após tempo expirar
                    user.setAccountLocked(false);
                    user.setLoginAttempts(0);
                    user.setAccountLockedAt(null);
                    userRepository.save(user);
                } else {
                    failureReason = "Account locked";
                    throw new AccountLockedException("Your account is locked. Please try again later.");
                }
            }

            success = passwordEncoder.matches(request.getPassword(), user.getPassword());

            if (!success) {
                failureReason = "Invalid password";
                int attempts = user.getLoginAttempts() + 1;
                user.setLoginAttempts(attempts);

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

            // Login OK: resetar tentativas
            user.setLoginAttempts(0);
            userRepository.save(user);

            // 2FA ativado?
            if (user.isTwoFactorEnabled()) {
                Pending2FALogin pending = new Pending2FALogin();
                pending.setId(UUID.randomUUID());
                pending.setUser(user);
                pending.setTempToken(UUID.randomUUID().toString());
                pending.setCreatedAt(LocalDateTime.now());
                pending.setExpiresAt(LocalDateTime.now().plusMinutes(10));
                pending2FALoginRepository.save(pending);

                // Não vamos gravar log para 2FA requirement, pois não é falha
                throw new TwoFactorRequiredException(
                        "Two-factor authentication is required.",
                        pending.getTempToken()
                );
            }

            // Geração de token e sessão
            String token = jwtTokenProvider.generateToken(
                    user.getId(),
                    user.getEmail(),
                    user.getRole().getName()
            );

            String sessionId = UUID.randomUUID().toString();
            activeSessionService.createSession(user, sessionId, servletRequest);

            activityLogService.logActivity(user, "Logged in successfully", servletRequest);

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            success = true;
            failureReason = null;
            // ✅ Login sucesso ➔ resetar tentativas
            user.setLoginAttempts(0);
            userRepository.save(user);

            // 🔄 Reseta contador por email após sucesso
            loginAttemptsPerEmail.remove(request.getEmail());

            return new LoginWithRefreshResponse(
                    token,
                    refreshToken.getToken(),
                    user.getUsername(),
                    user.getFullName(),
                    false
            );

        } finally {
            // Só grava se tivermos um usuário real
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

        if ("ADMIN".equalsIgnoreCase(request.getRole())) {
            throw new InvalidRoleAssignmentException("You cannot assign this role.");
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

    public LoginWithRefreshResponse refreshToken(RefreshTokenRequest request, HttpServletRequest servletRequest) {

        String ipAddress = metadataExtractor.getClientIp(servletRequest); // ou injete isso de alguma forma

        refreshAttemptsPerIp.merge(ipAddress, 1, Integer::sum);
        if (refreshAttemptsPerIp.get(ipAddress) > MAX_REFRESH_ATTEMPTS_PER_MINUTE) {
            throw new RateLimitExceededException("Too many refresh attempts. Please try again later.");
        }

        RefreshToken token = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RefreshTokenExpiredException("Invalid refresh token. Please login again."));

        // ✅ Verifica expiração
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenExpiredException("Refresh token expired. Please login again.");
        }

        User user = token.getUser();

        // 🔄 Refresh rotativo: invalida o token antigo
        refreshTokenRepository.deleteByUserId(user.getId()); // Limpa todos anteriores desse user

        // ✅ Gera novo refresh token
        RefreshToken newRefresh = new RefreshToken();
        newRefresh.setId(UUID.randomUUID());
        newRefresh.setToken(UUID.randomUUID().toString());
        newRefresh.setUser(user);
        newRefresh.setCreatedAt(LocalDateTime.now());
        newRefresh.setExpiresAt(LocalDateTime.now().plusDays(30)); // Exemplo de validade

        refreshTokenRepository.save(newRefresh);

        log.info("Refresh token used for user {}: new refresh token issued. New expires at: {}", user.getEmail(), newRefresh.getExpiresAt());

        // ✅ Gera novo access token
        String accessToken = jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getName()
        );

        activityLogService.logActivity(user, "Refreshed token successfully", servletRequest);

        return new LoginWithRefreshResponse(
                accessToken,
                newRefresh.getToken(),
                user.getUsername(),
                user.getFullName(),
                user.isTwoFactorEnabled()
        );
    }
}
