package com.controlcenter.admin.controller;

import com.controlcenter.admin.dto.AdminPermissionDTO;
import com.controlcenter.auth.service.LoginAttemptService;
import com.controlcenter.util.AuthTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AdminPermissionControllerTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("ecoprem_test_v01")
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
        baseUrl = "http://localhost:" + port + "/api/admin/permissions";
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
    }

    @BeforeEach
    void resetRateLimiting() {
        loginAttemptService.resetRateLimiting("127.0.0.1", "admin@example.com"); // ou email do usuário
    }

    private HttpHeaders getAdminHeaders() {
        return AuthTestUtils.getAuthenticatedHeaders(restTemplate, port, "admin@example.com", "Admin@123");
    }

    private HttpHeaders getUserHeaders() {
        return AuthTestUtils.getAuthenticatedHeaders(restTemplate, port, "user@example.com", "Admin@123");
    }

    private static UUID createdId;

    @Test
    void shouldReturn200_whenGetAllPermissions() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<AdminPermissionDTO[]> response = restTemplate.exchange(
                baseUrl, HttpMethod.GET, entity, AdminPermissionDTO[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturn200_whenSearchByPartialName() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<AdminPermissionDTO[]> response = restTemplate.exchange(
                baseUrl + "/search?name=read", HttpMethod.GET, entity, AdminPermissionDTO[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @Test
    @Order(1)
    void shouldCreatePermission() {
        AdminPermissionDTO dto = new AdminPermissionDTO();
        dto.setName("teste:view");
        dto.setDescription("Criada no teste");

        HttpEntity<AdminPermissionDTO> entity = new HttpEntity<>(dto, getAdminHeaders());

        ResponseEntity<AdminPermissionDTO> response = restTemplate.exchange(
                baseUrl , HttpMethod.POST, entity, AdminPermissionDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        createdId = response.getBody().getId(); // salvar para os próximos testes
    }

    @Test
    @Order(2)
    void shouldGetCreatedPermissionById() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<AdminPermissionDTO> response = restTemplate.exchange(
                baseUrl + "/" + "29d50804-b252-4e74-b8d8-ed26d1855fa9", HttpMethod.GET, entity, AdminPermissionDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("teste:view");
    }

    @Test
    @Order(3)
    void shouldUpdatePermission() {
        AdminPermissionDTO updateDto = new AdminPermissionDTO();
        updateDto.setName("teste:viewUpdated");
        updateDto.setDescription("Permissão atualizada");

        HttpEntity<AdminPermissionDTO> entity = new HttpEntity<>(updateDto, getAdminHeaders());

        ResponseEntity<AdminPermissionDTO> response = restTemplate.exchange(
                baseUrl + "/" + "29d50804-b252-4e74-b8d8-ed26d1855fa9", HttpMethod.PUT, entity, AdminPermissionDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("teste:viewUpdated");
    }

    @Test
    @Order(4)
    void shouldSearchByNameAfterUpdate() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<AdminPermissionDTO[]> response = restTemplate.exchange(
                baseUrl + "/search?name=updated", HttpMethod.GET, entity, AdminPermissionDTO[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()[0].getName()).contains("Updated");
    }

    @Test
    @Order(5)
    void shouldDeletePermission() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + "29d50804-b252-4e74-b8d8-ed26d1855fa9", HttpMethod.DELETE, entity, Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
