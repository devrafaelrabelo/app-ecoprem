package com.controlcenter.admin.controller;

import com.controlcenter.admin.dto.AdminRoleCreateUpdateDTO;
import com.controlcenter.admin.dto.AdminRoleDTO;
import com.controlcenter.admin.dto.AdminRoleResponseDTO;
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

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AdminRoleControllerTest {

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
        baseUrl = "http://localhost:" + port + "/api/admin/roles";
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
    @Order(1)
    void shouldCreateRole() {
        AdminRoleCreateUpdateDTO dto = new AdminRoleCreateUpdateDTO();
        dto.setName("INTEGRATION_ROLE_TEST");
        dto.setDescription("Criado no teste");
        dto.setSystemRole(false);
        dto.setPermissionIds(Set.of()); // sem permissões inicialmente

        HttpEntity<AdminRoleCreateUpdateDTO> entity = new HttpEntity<>(dto, getAdminHeaders());

        ResponseEntity<AdminRoleResponseDTO> response = restTemplate.exchange(
                baseUrl, HttpMethod.POST, entity, AdminRoleResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        createdId = response.getBody().getId();
    }

    @Test
    @Order(2)
    void shouldGetRoleById() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<AdminRoleResponseDTO> response = restTemplate.exchange(
                baseUrl + "/" + "5b423fc3-3f2a-4b02-afe0-bea9a68f5bd5", HttpMethod.GET, entity, AdminRoleResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("UPDATED_ROLE");
    }

    @Test
    @Order(3)
    void shouldUpdateRole() {
        AdminRoleCreateUpdateDTO updateDto = new AdminRoleCreateUpdateDTO();
        updateDto.setName("UPDATED_ROLE");
        updateDto.setDescription("Atualizado no teste");
        updateDto.setSystemRole(false);
        updateDto.setPermissionIds(Set.of());

        HttpEntity<AdminRoleCreateUpdateDTO> entity = new HttpEntity<>(updateDto, getAdminHeaders());

        ResponseEntity<AdminRoleResponseDTO> response = restTemplate.exchange(
                baseUrl + "/" + "5b423fc3-3f2a-4b02-afe0-bea9a68f5bd5", HttpMethod.PUT, entity, AdminRoleResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("UPDATED_ROLE");
    }

    @Test
    @Order(4)
    void shouldListAllRoles() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<AdminRoleDTO[]> response = restTemplate.exchange(
                baseUrl, HttpMethod.GET, entity, AdminRoleDTO[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    @Order(5)
    void shouldDeleteRole() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + "5b423fc3-3f2a-4b02-afe0-bea9a68f5bd5", HttpMethod.DELETE, entity, Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @Order(6)
    void shouldReturn404_whenGetDeletedRole() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/" + "5b423fc3-3f2a-4b02-afe0-bea9a68f5bd5", HttpMethod.GET, entity, String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
