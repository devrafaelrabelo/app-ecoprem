package com.controlcenter.auth.controller;

import com.controlcenter.auth.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AuthControllerLoginTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("ecoprem_test_v01")
            .withUsername("postgres")
            .withPassword("123456");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeAll
    static void startContainer() {
        postgres.start();
    }

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/auth";
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
    }


    @Test
    void shouldReturn200AndSetCookies_whenCredentialsAreValidAnd2FAIsDisabled() {
        LoginRequest request = new LoginRequest("valid_user@example.com", "Admin@123", false);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).isNotEmpty();
    }

    @Test
    void shouldReturn400_whenEmailIsInvalidFormat() {
        LoginRequest request = new LoginRequest("emailinvalido", "senha", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/login", entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturn401_whenUserDoesNotExistOrPasswordIsWrong() {
        LoginRequest request = new LoginRequest("naoexiste@example.com", "senhaErrada", false);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReturn403_whenAccountIsLocked() {
        LoginRequest request = new LoginRequest("locked_user@example.com", "Admin@123", false);

        ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl + "/login", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldReturn403_whenEmailIsNotVerified() {
        LoginRequest request = new LoginRequest("unverified_user@example.com", "Admin@123", false);

        ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl + "/login", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldReturn206AndSetTempToken_when2FAIsRequired() {
        LoginRequest request = new LoginRequest("2fa_required_user@example.com", "Admin@123", false);

        ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl + "/login", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PARTIAL_CONTENT);
        assertThat(response.getBody()).containsEntry("2fa_required", true);
    }

    @Test
    void shouldReturn400_whenPasswordIsBlank() {
        LoginRequest request = new LoginRequest("admin@example.com", "", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/login", entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturn200_whenLoginWithUsernameInsteadOfEmail() {
        LoginRequest request = new LoginRequest("admin", "Admin@123", false);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldSetLongerExpiry_whenRememberMeIsTrue() {
        LoginRequest request = new LoginRequest("admin@example.com", "Admin@123", true);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).isNotEmpty();
    }

    @Test
    void shouldReturn400_whenJsonHasUnexpectedField() {
        String invalidJson = """
        {
            "email": "admin@example.com",
            "password": "Admin@123",
            "remember": false,
            "extra": "should not be here"
        }
        """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(invalidJson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/login", entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}

