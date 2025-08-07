////package com.controlcenter.auth.service;
////
////import com.controlcenter.auth.dto.LoginRequest;
////import com.controlcenter.auth.dto.LoginResult;
////import com.controlcenter.auth.entity.Role;
////import com.controlcenter.auth.entity.User;
////import com.controlcenter.auth.exception.*;
////import com.controlcenter.auth.repository.LoginHistoryRepository;
////import com.controlcenter.auth.repository.Pending2FALoginRepository;
////import com.controlcenter.auth.repository.RoleRepository;
////import com.controlcenter.user.repository.UserRepository;
////import com.controlcenter.auth.security.JwtTokenProvider;
////import com.controlcenter.auth.util.LoginMetadataExtractor;
////import com.github.benmanes.caffeine.cache.Cache;
////import jakarta.servlet.http.HttpServletRequest;
////import org.junit.jupiter.api.Test;
////import org.junit.jupiter.api.extension.ExtendWith;
////import org.mockito.InjectMocks;
////import org.mockito.Mock;
////import org.mockito.Mockito;
////import org.mockito.junit.jupiter.MockitoExtension;
////import org.springframework.security.crypto.password.PasswordEncoder;
////import static org.junit.jupiter.api.Assertions.assertThrows;
////import static org.assertj.core.api.Assertions.assertThatThrownBy;
////
////import java.time.LocalDateTime;
////import java.util.Optional;
////
////import static org.junit.jupiter.api.Assertions.*;
////import static org.mockito.ArgumentMatchers.*;
////import static org.mockito.Mockito.*;
////
////@ExtendWith(MockitoExtension.class)
////class AuthServiceLoginTests extends AuthServiceTestBase {
//
//import com.controlcenter.user.repository.UserRepository;
//import org.mockito.Mock;
//
//@Mock private UserRepository userRepository;
//    @Mock
//    private RoleRepository roleRepository;
//    @Mock private PasswordEncoder passwordEncoder;
//    @Mock private JwtTokenProvider jwtTokenProvider;
//    @Mock private LoginHistoryRepository loginHistoryRepository; // <--- ADICIONE ESTE
//    @Mock private LoginMetadataExtractor metadataExtractor;
//    @Mock private ActivityLogService activityLogService;
//    @Mock private ActiveSessionService activeSessionService;
//    @Mock private Pending2FALoginRepository pending2FALoginRepository;
//    @Mock private MailService mailService;
//    @Mock private RefreshTokenService refreshTokenService;
//    @Mock private Cache<String, Integer> loginAttemptsPerIp;
//    @Mock private Cache<String, Integer> loginAttemptsPerEmail;
//    @Mock private Cache<String, Integer> refreshAttemptsPerIp;
//    @Mock private HttpServletRequest servletRequest;
//
//    // Outros mocks do AuthService se necessário...
//
//    @InjectMocks
//    private AuthService authService;
//
//    @Test
//    void testLoginSuccess() {
//        // Dados do teste
//        String email = "user@empresa.com";
//        String senha = "senhaForte123";
//        String senhaCriptografada = "senhaHash";
//        String tokenGerado = "jwt-token-mock";
//        String ipAddress = "127.0.0.1";
//
//        Role role = new Role();
//        role.setName("USER");
//
//        User usuario = new User();
//        usuario.setEmail(email);
//        usuario.setPassword(senhaCriptografada);
//        usuario.setEmailVerified(true);
//        usuario.setRole(role);
//        usuario.setLoginAttempts(0);
//        usuario.setAccountLocked(false);
//
//        LoginRequest request = new LoginRequest();
//        request.setEmail(email);
//        request.setPassword(senha);
//
//        // Mockando dependências
//        when(userRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
//        when(passwordEncoder.matches(senha, senhaCriptografada)).thenReturn(true);
//        when(jwtTokenProvider.generateToken(any(), eq(email), eq("USER"))).thenReturn(tokenGerado);
//        when(metadataExtractor.getClientIp(servletRequest)).thenReturn(ipAddress);
//
//        // Aqui estão os stubbings que evitam o erro do cache
//        lenient().when(loginAttemptsPerIp.get(anyString(), any())).thenReturn(0);
//        lenient().when(loginAttemptsPerEmail.get(anyString(), any())).thenReturn(0);
//
//        // Act (executa o método)
//        LoginResult resultado = authService.login(request, servletRequest);
//
//        // Assert (verifica o resultado)
//        assertNotNull(resultado);
//        assertEquals(tokenGerado, resultado.response().getAccessToken());
//        assertEquals(email, resultado.user().getEmail());
//    }
//
//    @Test
//    void testLoginWithInvalidEmail_shouldThrowInvalidRequestException() {
//        LoginRequest request = new LoginRequest();
//        request.setEmail("email-invalido");
//        request.setPassword("senha");
//
//        assertThrows(InvalidRequestException.class, () -> {
//            authService.login(request, servletRequest);
//        });
//    }
//
//    @Test
//    void testLoginUserNotFound_shouldThrowInvalidCredentialsException() {
//        String email = "user@empresa.com";
//        LoginRequest request = new LoginRequest();
//        request.setEmail(email);
//        request.setPassword("senha");
//
//        String ip = "127.0.0.1";
//        when(metadataExtractor.getClientIp(servletRequest)).thenReturn(ip);
//        lenient().when(loginAttemptsPerIp.getIfPresent(anyString())).thenReturn(0);
//        lenient().when(loginAttemptsPerEmail.getIfPresent(anyString())).thenReturn(0);
//        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
//
//        assertThrows(InvalidCredentialsException.class, () -> {
//            authService.login(request, servletRequest);
//        });
//    }
//
//    @Test
//    void testLoginEmailNotVerified_shouldThrowEmailNotVerifiedException() {
//        String email = "user@empresa.com";
//        User user = new User();
//        user.setEmail(email);
//        user.setEmailVerified(false);
//
//        LoginRequest request = new LoginRequest();
//        request.setEmail(email);
//        request.setPassword("senha");
//
//        String ip = "127.0.0.1";
//        when(metadataExtractor.getClientIp(servletRequest)).thenReturn(ip);
//        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
//
//        assertThrows(EmailNotVerifiedException.class, () -> {
//            authService.login(request, servletRequest);
//        });
//    }
//
//    @Test
//    void testLoginAccountLocked_shouldThrowAccountLockedException() {
//        String email = "user@empresa.com";
//        User user = new User();
//        user.setEmail(email);
//        user.setEmailVerified(true);
//        user.setAccountLocked(true);
//        user.setAccountLockedAt(LocalDateTime.now()); // bloqueado agora
//
//        LoginRequest request = new LoginRequest();
//        request.setEmail(email);
//        request.setPassword("senha");
//
//        String ip = "127.0.0.1";
//        when(metadataExtractor.getClientIp(servletRequest)).thenReturn(ip);
//        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
//
//        assertThrows(AccountLockedException.class, () -> {
//            authService.login(request, servletRequest);
//        });
//    }
//
//    @Test
//    void testLoginInvalidPassword_shouldThrowInvalidCredentialsException() {
//        String email = "user@empresa.com";
//        String senhaCorreta = "senha123";
//        String senhaErrada = "outraSenha";
//
//        User user = new User();
//        user.setEmail(email);
//        user.setEmailVerified(true);
//        user.setPassword(senhaCorreta);
//        user.setLoginAttempts(0);
//        user.setAccountLocked(false);
//
//        LoginRequest request = new LoginRequest();
//        request.setEmail(email);
//        request.setPassword(senhaErrada);
//
//        String ip = "127.0.0.1";
//        when(metadataExtractor.getClientIp(servletRequest)).thenReturn(ip);
//        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
//        when(passwordEncoder.matches(senhaErrada, senhaCorreta)).thenReturn(false);
//
//        assertThrows(InvalidCredentialsException.class, () -> {
//            authService.login(request, servletRequest);
//        });
//    }
//
//    @Test
//    void testLoginTooManyAttemptsIp_shouldThrowRateLimitExceededException() {
//        String email = "user@empresa.com";
//        LoginRequest request = new LoginRequest();
//        request.setEmail(email);
//        request.setPassword("senha");
//
//        String ip = "127.0.0.1";
//
//
//
//        when(metadataExtractor.getClientIp(servletRequest)).thenReturn(ip);
//        when(loginAttemptsPerIp.getIfPresent(ip)).thenReturn(10); // já no limite → login() soma +1 → 11
//        when(loginAttemptsPerEmail.getIfPresent(email)).thenReturn(0); // evitar cair no segundo limite
//        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User())); // evita cair em InvalidCredentialsException
//
//        assertThrows(RateLimitExceededException.class, () -> {
//            authService.login(request, servletRequest);
//        });
//    }
//
//    @Test
//    void shouldThrowWhenIpRateLimitExceeded() {
//        when(loginAttemptsPerIp.getIfPresent(testIp)).thenReturn(10); // já no limite
//
//        User user = createVerifiedUser();
//        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));
//        when(passwordEncoder.matches(any(), any())).thenReturn(true);
//
//        assertThrows(RateLimitExceededException.class, () -> {
////            authService.login(createLoginRequest(), servletRequest);
////        });
////    }
////
////    @Test
////    void testLoginTooManyAttemptsEmail() {
////        // Arrange
////        String email = "user@teste.com";
////        LoginRequest request = new LoginRequest();
////        request.setEmail(email);
////        request.setPassword("senhaerrada");
////
////        HttpServletRequest servletRequest = Mockito.mock(HttpServletRequest.class);
////        Mockito.when(metadataExtractor.getClientIp(servletRequest)).thenReturn("192.168.1.10");
////        Mockito.when(metadataExtractor.getUserAgent(servletRequest)).thenReturn("JUnit/Mock");
////
////        // Força sempre retorno vazio (usuário não existe)
////        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
////
////        for (int i = 1; i <= 10; i++) {
////            try { authService.login(request, servletRequest); }
////            catch (Exception ignored) { }
////        }
////
////        // Só na 11ª tentativa é que dá rate limit!
////        assertThatThrownBy(() -> authService.login(request, servletRequest))
////                .isInstanceOf(RateLimitExceededException.class)
////                .hasMessageContaining("Too many login attempts for this account");
////    }
////}
//
//
//package com.controlcenter.auth.service;
//
//import com.controlcenter.auth.config.AuthProperties;
//import com.controlcenter.auth.dto.LoginRequest;
//import com.controlcenter.auth.dto.LoginResult;
//import com.controlcenter.auth.exception.InvalidCredentialsException;
//import com.controlcenter.exceptions.exception.TwoFactorRequiredException;
//import com.controlcenter.auth.repository.LoginHistoryRepository;
//import com.controlcenter.auth.repository.Pending2FALoginRepository;
//import com.controlcenter.auth.repository.RefreshTokenRepository;
//import com.controlcenter.auth.security.JwtTokenProvider;
//import com.controlcenter.auth.util.JwtCookieUtil;
//import com.controlcenter.auth.util.LoginMetadataExtractor;
//import com.controlcenter.entity.user.User;
//import com.controlcenter.user.repository.UserRepository;
//import com.controlcenter.user.service.UserService;
//import jakarta.servlet.http.HttpServletRequest;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
//import static org.mockito.Mockito.*;
//
//class AuthServiceLoginTests {
//
//    private UserRepository userRepository;
//    private PasswordEncoder passwordEncoder;
//    private LoginAttemptService loginAttemptService;
//    private UserValidationService userValidationService;
//    private LoginMetadataExtractor metadataExtractor;
//
//    private AuthService authService;
//    private LoginHistoryRepository loginHistoryRepository;
//    private Pending2FALoginRepository pending2FALoginRepository;
//    private RefreshTokenRepository refreshTokenRepository;
//    private JwtTokenProvider jwtTokenProvider;
//    private AuthProperties authProperties;
//    private JwtCookieUtil jwtCookieUtil;
//    private ActivityLogService activityLogService;
//    private RevokedTokenService revokedTokenService;
//    private SessionService sessionService;
//    private UserService userService;
//    private TokenService tokenService;
//
//    @BeforeEach
//    void setUp() {
//        userRepository = mock(UserRepository.class);
//        loginHistoryRepository = mock(LoginHistoryRepository.class);
//        pending2FALoginRepository = mock(Pending2FALoginRepository.class);
//        refreshTokenRepository = mock(RefreshTokenRepository.class);
//        passwordEncoder = mock(PasswordEncoder.class);
//        metadataExtractor = mock(LoginMetadataExtractor.class);
//        jwtTokenProvider = mock(JwtTokenProvider.class);
//        authProperties = mock(AuthProperties.class);
//        jwtCookieUtil = mock(JwtCookieUtil.class);
//        activityLogService = mock(ActivityLogService.class);
//        revokedTokenService = mock(RevokedTokenService.class);
//        loginAttemptService = mock(LoginAttemptService.class);
//        userValidationService = mock(UserValidationService.class);
//        sessionService = mock(SessionService.class);
//        userService = mock(UserService.class);
//        tokenService = mock(TokenService.class);
//
//        authService = new AuthService(
//                userRepository,
//                loginHistoryRepository,
//                pending2FALoginRepository,
//                refreshTokenRepository,
//                passwordEncoder,
//                metadataExtractor,
//                jwtTokenProvider,
//                authProperties,
//                jwtCookieUtil,
//                activityLogService,
//                revokedTokenService,
//                loginAttemptService,
//                userValidationService,
//                sessionService,
//                userService,
//                tokenService
//        );
//    }
//
//    @Test
//    void shouldReturnLoginResult_whenCredentialsAreValidAnd2FAIsDisabled() {
//        // Arrange
//        String email = "admin@example.com";
//        String rawPassword = "Admin@123";
//        String encodedPassword = "$2a$12$EXM5g9yGl16L1G0jLcn0EunGo57X4VB4xb4.xI9Z/QWHMg0cmeNTS";
//
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
//        when(request.getHeader("User-Agent")).thenReturn("JUnitTest");
//
//        User user = new User();
//        user.setId(UUID.randomUUID());
//        user.setEmail(email);
//        user.setPassword(encodedPassword);
//        user.setTwoFactorEnabled(false);
//
//        when(userRepository.findByEmailWithStatusAndRoles(email)).thenReturn(Optional.of(user));
//        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
//
//        // Act
//        LoginRequest loginRequest = new LoginRequest(email, rawPassword, false);
//        LoginResult result = authService.login(loginRequest, request);
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(result.user()).isEqualTo(user);
//        assertThat(result.response()).isNull(); // token é gerado fora
//    }
//
//    @Test
//    void shouldThrowInvalidCredentialsException_whenPasswordIsIncorrect() {
//        // Arrange
//        String email = "admin@example.com";
//        String wrongPassword = "Wrong@123";
//        String encodedPassword = "$2a$12$EXM5g9yGl16L1G0jLcn0EunGo57X4VB4xb4.xI9Z/QWHMg0cmeNTS"; // hash de Admin@123
//
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        User user = new User();
//        user.setEmail(email);
//        user.setPassword(encodedPassword);
//        user.setTwoFactorEnabled(false);
//
//        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
//        when(request.getHeader("User-Agent")).thenReturn("JUnitTest");
//
//        when(userRepository.findByEmailWithStatusAndRoles(email)).thenReturn(Optional.of(user));
//        when(passwordEncoder.matches(eq(wrongPassword), eq(encodedPassword))).thenReturn(false);
//
//        // Act & Assert
//        assertThatThrownBy(() -> authService.login(new LoginRequest(email, wrongPassword, false), request))
//                .isInstanceOf(InvalidCredentialsException.class)
//                .hasMessageContaining("Invalid email or password");
//    }
//
//    @Test
//    void shouldThrowInvalidCredentialsException_whenEmailDoesNotExist() {
//        // Arrange
//        String email = "naoexiste@example.com";
//        String password = "qualquerSenha";
//
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
//        when(request.getHeader("User-Agent")).thenReturn("JUnitTest");
//
//        when(userRepository.findByEmailWithStatusAndRoles(email)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThatThrownBy(() -> authService.login(new LoginRequest(email, password, false), request))
//                .isInstanceOf(InvalidCredentialsException.class)
//                .hasMessageContaining("Invalid email or password");
//    }
//
//    @Test
//    void shouldThrowTwoFactorRequiredException_when2FAIsEnabled() {
//        String email = "2fa@example.com";
//        String password = "Admin@123";
//        String encodedPassword = "$2a$12$EXM5g9yGl16L1G0jLcn0EunGo57X4VB4xb4.xI9Z/QWHMg0cmeNTS";
//
//        User user = new User();
//        user.setId(UUID.randomUUID());
//        user.setEmail(email);
//        user.setPassword(encodedPassword);
//        user.setTwoFactorEnabled(true);
//
//        when(userRepository.findByEmailWithStatusAndRoles(email)).thenReturn(Optional.of(user));
//        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
//
//        assertThatThrownBy(() ->
//                authService.login(new LoginRequest(email, password, false), mock(HttpServletRequest.class))
//        ).isInstanceOf(TwoFactorRequiredException.class)
//                .hasMessageContaining("2FA required");
//    }
//}
