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
    private final Map<String, Integer> loginAttemptsPerIp = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS_PER_MINUTE = 10;

    private final RefreshTokenService refreshTokenService;

    // Login (refatorado)
    public LoginWithRefreshResponse login(LoginRequest request, HttpServletRequest servletRequest) {

        // ✅ Validar email logo no começo
        if (!isValidEmail(request.getEmail())) {
            throw new InvalidRequestException("Invalid email format.");
        }

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        String userAgent = metadataExtractor.getUserAgent(servletRequest);
        String ipAddress = metadataExtractor.getClientIp(servletRequest);

        // Limite de tentativas de login por IP
        loginAttemptsPerIp.merge(ipAddress, 1, Integer::sum);
        if (loginAttemptsPerIp.get(ipAddress) > MAX_ATTEMPTS_PER_MINUTE) {
            throw new RateLimitExceededException("Too many login attempts. Please try again later.");
        }

        // Se usuário não existe ➔ grava tentativa e retorna erro padronizado
        if (userOpt.isEmpty()) {
            recordLoginAttempt(null, ipAddress, userAgent, false);
            throw new InvalidCredentialsException("The email or password you entered is incorrect.");
        }

        User user = userOpt.get();

        // ✅ Verifica se email está verificado
        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("Please verify your email before logging in.");
        }

        // ✅ Verifica se a conta está suspensa
        if (user.getUserStatus() != null &&
                "suspended".equalsIgnoreCase(user.getUserStatus().getStatus())) {
            throw new AccountSuspendedException("Your account has been suspended. Please contact support.");
        }

        if (user.getUserStatus() != null &&
                "deactivated".equalsIgnoreCase(user.getUserStatus().getStatus())) {
            throw new AccountNotActiveException("Your account is deactivated. Please contact support.");
        }

        // ✅ Checa se a conta está bloqueada
        if (user.isAccountLocked()) {
            if (user.getAccountLockedAt() != null &&
                    user.getAccountLockedAt().plusMinutes(15).isBefore(LocalDateTime.now())) {
                // Desbloquear após tempo expirar
                user.setAccountLocked(false);
                user.setLoginAttempts(0);
                user.setAccountLockedAt(null);
                userRepository.save(user);
            } else {
                throw new AccountLockedException("Your account is locked. Please try again later.");
            }
        }

        // ✅ Valida senha
        boolean success = passwordEncoder.matches(request.getPassword(), user.getPassword());

        // ✅ Grava tentativa (sempre)
        recordLoginAttempt(user, ipAddress, userAgent, success);

        // Se senha incorreta ➔ incrementa tentativas e bloqueia se necessário
        if (!success) {
            int attempts = user.getLoginAttempts() + 1;
            user.setLoginAttempts(attempts);

            if (attempts >= 5 && !user.isAccountLocked()) {
                user.setAccountLocked(true);
                user.setAccountLockedAt(LocalDateTime.now());

                // Enviar e-mail de bloqueio
                mailService.sendAccountLockedEmail(user.getEmail(), user.getUsername());
            }

            userRepository.save(user);

            if (user.isAccountLocked()) {
                throw new AccountLockedException("Your account is locked. Please try again later.");
            } else {
                throw new InvalidCredentialsException("The email or password you entered is incorrect.");
            }
        }

        // ✅ Login sucesso ➔ resetar tentativas
        user.setLoginAttempts(0);
        userRepository.save(user);

        // Se 2FA está ativado ➔ retorna tempToken (não gera token ainda)
        if (user.isTwoFactorEnabled()) {
            Pending2FALogin pending = new Pending2FALogin();
            pending.setId(UUID.randomUUID());
            pending.setUser(user);
            pending.setTempToken(UUID.randomUUID().toString());
            pending.setCreatedAt(LocalDateTime.now());
            pending.setExpiresAt(LocalDateTime.now().plusMinutes(10)); // expira em 10 min

            pending2FALoginRepository.save(pending);

            throw new TwoFactorRequiredException(
                    "Two-factor authentication is required.",
                    pending.getTempToken()
            );
        }

        // Se NÃO tem 2FA ➔ faz login completo
        String token = jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getName()
        );

        // Cria ActiveSession
        String sessionId = UUID.randomUUID().toString();
        activeSessionService.createSession(user, sessionId, servletRequest);

        activityLogService.logActivity(user, "Logged in successfully", servletRequest);


        // 🔄 Gera Refresh Token também
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new LoginWithRefreshResponse(
                token,
                refreshToken.getToken(),
                user.getUsername(),
                user.getFullName(),
                false
        );
    }

    // 🔧 Extrai a lógica de gravação de LoginHistory para evitar duplicação
    private void recordLoginAttempt(User user, String ipAddress, String userAgent, boolean success) {
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
        loginHistoryRepository.save(history);
    }

    // Register (mantido igual)
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
        newUser.setFullName(
                request.getFullName() != null ? request.getFullName() :
                        request.getFirstName() + " " + request.getLastName()
        );
        newUser.setSocialName(request.getSocialName());
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(role);
        newUser.setEmailVerified(false); // default
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        userRepository.save(newUser);
    }

}
