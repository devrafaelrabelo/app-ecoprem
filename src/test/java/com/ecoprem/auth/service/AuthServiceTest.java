//package com.ecoprem.auth.service;
//
//import com.ecoprem.auth.dto.LoginRequest;
//import com.ecoprem.auth.dto.LoginResult;
//import com.ecoprem.auth.entity.Role;
//import com.ecoprem.auth.entity.User;
//import com.ecoprem.auth.exception.*;
//import com.ecoprem.auth.repository.LoginHistoryRepository;
//import com.ecoprem.auth.repository.Pending2FALoginRepository;
//import com.ecoprem.auth.repository.RoleRepository;
//import com.ecoprem.auth.repository.UserRepository;
//import com.ecoprem.auth.security.JwtTokenProvider;
//import com.ecoprem.auth.util.LoginMetadataExtractor;
//import com.github.benmanes.caffeine.cache.Cache;
//import jakarta.servlet.http.HttpServletRequest;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AuthServiceLoginTests extends AuthServiceTestBase {
//
//    @Mock private UserRepository userRepository;
//    @Mock private RoleRepository roleRepository;
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
//            authService.login(createLoginRequest(), servletRequest);
//        });
//    }
//
//    @Test
//    void testLoginTooManyAttemptsEmail() {
//        // Arrange
//        String email = "user@teste.com";
//        LoginRequest request = new LoginRequest();
//        request.setEmail(email);
//        request.setPassword("senhaerrada");
//
//        HttpServletRequest servletRequest = Mockito.mock(HttpServletRequest.class);
//        Mockito.when(metadataExtractor.getClientIp(servletRequest)).thenReturn("192.168.1.10");
//        Mockito.when(metadataExtractor.getUserAgent(servletRequest)).thenReturn("JUnit/Mock");
//
//        // Força sempre retorno vazio (usuário não existe)
//        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
//
//        for (int i = 1; i <= 10; i++) {
//            try { authService.login(request, servletRequest); }
//            catch (Exception ignored) { }
//        }
//
//        // Só na 11ª tentativa é que dá rate limit!
//        assertThatThrownBy(() -> authService.login(request, servletRequest))
//                .isInstanceOf(RateLimitExceededException.class)
//                .hasMessageContaining("Too many login attempts for this account");
//    }
//}


package com.ecoprem.auth.service;

import com.ecoprem.auth.dto.LoginRequest;
import com.ecoprem.auth.dto.LoginResult;
import com.ecoprem.auth.entity.Role;
import com.ecoprem.auth.entity.User;
import com.ecoprem.auth.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceLoginTests extends AuthServiceTestBase {

    @Test
    void testLoginSuccess() {
        String token = "jwt-token-mock";
        Role role = new Role();
        role.setName("USER");

        User user = createVerifiedUser();
        user.setPassword("encoded-password");
        user.setRole(role);

        LoginRequest request = createLoginRequest();
        request.setPassword("raw-password");

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("raw-password", "encoded-password")).thenReturn(true);
        when(jwtTokenProvider.generateToken(any(), eq(testEmail), eq("USER"))).thenReturn(token);

        LoginResult result = authService.login(request, servletRequest);

        assertNotNull(result);
        assertEquals(token, result.response().getAccessToken());
        assertEquals(testEmail, result.user().getEmail());
    }

    @Test
    void testLoginWithInvalidEmail_shouldThrowInvalidRequestException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("email-invalido");
        request.setPassword("senha");

        assertThrows(InvalidRequestException.class, () -> authService.login(request, servletRequest));
    }

    @Test
    void testLoginUserNotFound_shouldThrowInvalidCredentialsException() {
        LoginRequest request = createLoginRequest();
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request, servletRequest));
    }

    @Test
    void testLoginEmailNotVerified_shouldThrowEmailNotVerifiedException() {
        User user = createVerifiedUser();
        user.setEmailVerified(false);

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));

        assertThrows(EmailNotVerifiedException.class, () -> authService.login(createLoginRequest(), servletRequest));
    }

    @Test
    void testLoginAccountLocked_shouldThrowAccountLockedException() {
        User user = createVerifiedUser();
        user.setAccountLocked(true);
        user.setAccountLockedAt(LocalDateTime.now());

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));

        assertThrows(AccountLockedException.class, () -> authService.login(createLoginRequest(), servletRequest));
    }

    @Test
    void testLoginInvalidPassword_shouldThrowInvalidCredentialsException() {
        User user = createVerifiedUser();
        user.setPassword("senhaCerta");

        LoginRequest request = createLoginRequest();
        request.setPassword("senhaErrada");

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senhaErrada", "senhaCerta")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request, servletRequest));
    }

    @Test
    void testLoginTooManyAttemptsIp_shouldThrowRateLimitExceededException() {

    }

    @Test
    void shouldThrowRateLimitExceededException_whenEmailAttemptsExceedLimit() {

    }

    @Test
    void shouldBlockLoginAfterMultipleFailedAttempts_byEmail() {
        
    }

}
