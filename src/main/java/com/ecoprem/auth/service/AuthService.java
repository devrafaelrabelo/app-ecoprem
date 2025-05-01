package com.ecoprem.auth.service;

import com.ecoprem.auth.dto.*;
import com.ecoprem.auth.entity.*;
import com.ecoprem.auth.repository.*;
import com.ecoprem.auth.security.JwtTokenProvider;
import com.ecoprem.auth.util.LoginMetadataExtractor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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

    // Login (refatorado)
    public LoginResponse login(LoginRequest request, HttpServletRequest servletRequest) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        String userAgent = metadataExtractor.getUserAgent(servletRequest);
        String ipAddress = metadataExtractor.getClientIp(servletRequest);

        // Se usuário não existe ➔ grava tentativa e retorna erro
        if (userOpt.isEmpty()) {
            LoginHistory history = new LoginHistory();
            history.setId(UUID.randomUUID());
            history.setUser(null);  // user não existe
            history.setLoginDate(LocalDateTime.now());
            history.setIpAddress(ipAddress);
            history.setLocation(metadataExtractor.getLocation(ipAddress));
            history.setDevice(metadataExtractor.detectDevice(userAgent));
            history.setBrowser(metadataExtractor.detectBrowser(userAgent));
            history.setOperatingSystem(metadataExtractor.detectOS(userAgent));
            history.setSuccess(false);
            loginHistoryRepository.save(history);

            throw new RuntimeException("Invalid credentials");
        }

        User user = userOpt.get();

        // 1️⃣ Checar se a conta está bloqueada
        if (user.isAccountLocked()) {
            if (user.getAccountLockedAt() != null &&
                    user.getAccountLockedAt().plusMinutes(15).isBefore(LocalDateTime.now())) {
                // Desbloquear após tempo expirar
                user.setAccountLocked(false);
                user.setLoginAttempts(0);
                user.setAccountLockedAt(null);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Account is locked. Please try again later.");
            }
        }

        // 2️⃣ Validar senha
        boolean success = passwordEncoder.matches(request.getPassword(), user.getPassword());

        // 3️⃣ Gravar tentativa no LoginHistory
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

        // 4️⃣ Se senha incorreta ➔ incrementa tentativas e bloqueia se necessário
        if (!success) {
            int attempts = user.getLoginAttempts() + 1;
            user.setLoginAttempts(attempts);

            if (attempts >= 5 && !user.isAccountLocked()) {
                user.setAccountLocked(true);
                user.setAccountLockedAt(LocalDateTime.now());

                // Enviar o e-mail de notificação
                mailService.sendAccountLockedEmail(user.getEmail(), user.getUsername());
            }

            userRepository.save(user);

            if (user.isAccountLocked()) {
                throw new RuntimeException("Account is locked. Please check your email or try again later.");
            } else {
                throw new RuntimeException("Invalid credentials");
            }
        }

        // 5️⃣ Login sucesso ➔ resetar tentativas
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

            return new LoginResponse(
                    null,  // token ainda não gerado
                    user.getUsername(),
                    user.getFullName(),
                    true,
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

        return new LoginResponse(
                token,
                user.getUsername(),
                user.getFullName(),
                false,
                null
        );
    }

    // Register
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already in use");
        }

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found: " + request.getRole()));

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

    // Two-Factor Auth (simplificado)
    public void verifyTwoFactor(TwoFactorRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // TODO: implementar validação real do código 2FA
        if (!"123456".equals(request.getCode())) {
            throw new RuntimeException("Invalid 2FA code");
        }

        // Success: poderia marcar algo no banco, se necessário
    }
}