package com.controlcenter.admin.controller;

import com.controlcenter.admin.dto.SessionInfoDTO;
import com.controlcenter.admin.dto.SessionRevokeAuditDTO;
import com.controlcenter.auth.service.LoginAttemptService;
import com.controlcenter.util.AuthTestUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AdminSessionControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("ecoprem_test_sessions")
            .withUsername("postgres")
            .withPassword("123456");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LoginAttemptService loginAttemptService;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/admin/sessions";
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
    }

    @BeforeEach
    void resetRateLimiting() {
        loginAttemptService.resetRateLimiting("127.0.0.1", "admin@example.com");
    }

    private HttpHeaders getAdminHeaders() {
        return AuthTestUtils.getAuthenticatedHeaders(restTemplate, port, "admin@example.com", "Admin@123");
    }

    @Test
    void shouldReturnAllSessions_whenAdminAuthenticated() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<SessionInfoDTO[]> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                entity,
                SessionInfoDTO[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldReturnLastSessionOfUser() {
        UUID userId = UUID.fromString("e6e28546-5601-4840-9804-f61ea2e55c4a"); // exemplo
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<SessionInfoDTO> response = restTemplate.exchange(
                baseUrl + "/user/" + userId + "/last",
                HttpMethod.GET,
                entity,
                SessionInfoDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldRevokeSessionById() {
        UUID sessionId = UUID.fromString("0e12f82a-5710-4e5c-89df-f1d0436b53a5"); // exemplo
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/revoke/" + sessionId,
                HttpMethod.DELETE,
                entity,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void shouldReturnExpiredSessions() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<SessionInfoDTO[]> response = restTemplate.exchange(
                baseUrl + "/expired",
                HttpMethod.GET,
                entity,
                SessionInfoDTO[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldReturnAuditLog() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<SessionRevokeAuditDTO[]> response = restTemplate.exchange(
                baseUrl + "/audit-log",
                HttpMethod.GET,
                entity,
                SessionRevokeAuditDTO[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldRevokeAllSessionsOfUser() {
        UUID userId = UUID.fromString("e6e28546-5601-4840-9804-f61ea2e55c4a"); // substitua por um user válido no seed
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/revoke-all/" + userId,
                HttpMethod.DELETE,
                entity,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}