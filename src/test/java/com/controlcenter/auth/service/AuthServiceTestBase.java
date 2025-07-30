package com.controlcenter.auth.service;

import com.controlcenter.auth.dto.LoginRequest;
import com.controlcenter.entity.user.User;
import com.controlcenter.auth.repository.*;
import com.controlcenter.auth.security.JwtTokenProvider;
import com.controlcenter.auth.util.LoginMetadataExtractor;
import com.controlcenter.user.repository.UserRepository;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTestBase {

    @Mock protected UserRepository userRepository;
    @Mock protected RoleRepository roleRepository;
    @Mock protected PasswordEncoder passwordEncoder;
    @Mock protected JwtTokenProvider jwtTokenProvider;
    @Mock protected LoginHistoryRepository loginHistoryRepository;
    @Mock protected LoginMetadataExtractor metadataExtractor;
    @Mock protected ActivityLogService activityLogService;
    @Mock protected ActiveSessionService activeSessionService;
    @Mock protected Pending2FALoginRepository pending2FALoginRepository;
    @Mock protected MailService mailService;
    @Mock protected RefreshTokenService refreshTokenService;
    @Mock protected RefreshTokenRepository refreshTokenRepository;
    @Mock protected Cache<String, Integer> loginAttemptsPerIp;
    @Mock protected Cache<String, Integer> loginAttemptsPerEmail;
    @Mock protected Cache<String, Integer> refreshAttemptsPerIp;
    @Mock protected HttpServletRequest servletRequest;

    @InjectMocks protected AuthService authService;

    protected final String testEmail = "user@empresa.com";
    protected final String testIp = "127.0.0.1";

    @BeforeEach
    void setupCommonMocks() {
        when(metadataExtractor.getClientIp(servletRequest)).thenReturn(testIp);
        when(metadataExtractor.getUserAgent(servletRequest)).thenReturn("JUnit-Test");

        lenient().when(loginAttemptsPerIp.getIfPresent(testIp)).thenReturn(0);
        lenient().when(loginAttemptsPerEmail.getIfPresent(testEmail)).thenReturn(0);
    }

    protected User createVerifiedUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .email(testEmail)
                .emailVerified(true)
                .accountLocked(false)
                .loginAttempts(0)
                .build();
    }

    protected LoginRequest createLoginRequest() {
        LoginRequest request = new LoginRequest();
        request.setEmail(testEmail);
        request.setPassword("senha");
        return request;
    }
}
