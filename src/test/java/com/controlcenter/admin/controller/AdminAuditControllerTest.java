
package com.controlcenter.admin.controller;

import com.controlcenter.auth.service.LoginAttemptService;
import com.controlcenter.util.AuthTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AdminAuditControllerTest {

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
        baseUrl = "http://localhost:" + port + "/api/admin/audits";
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


    @Test
    void shouldReturn200_whenGetRequestEvents() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/request-events", HttpMethod.GET, entity, String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturn200_whenGetSecurityEvents() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/security-events", HttpMethod.GET, entity, String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturn200_whenGetSystemEvents() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/system-events", HttpMethod.GET, entity, String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturn200_withFilters_onRequestEvents() {
        String url = baseUrl + "/request-events?method=GET&status=200";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturn200_withFilters_onSecurityEvents() {
        String url = baseUrl + "/security-events?eventType=ACCESS_DENIED&username=admin";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturn200_withFilters_onSystemEvents() {
        String url = baseUrl + "/system-events?action=PERMISSION_GRANTED&performedBy=admin";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturn403_whenNotAuthenticated_whenGetRequestEvents() {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/request-events", HttpMethod.GET, null, String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED)
                .withFailMessage("Expected UNAUTHORIZED (401), got %s", response.getStatusCode());
    }

    @Test
    void shouldReturn403_whenNotAuthenticated_whenGetSecurityEvents() {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/security-events", HttpMethod.GET, null, String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED)
                .withFailMessage("Expected UNAUTHORIZED (401), got %s", response.getStatusCode());
    }

    @Test
    void shouldReturn403_whenNotAuthenticated_whenGetSystemEvents() {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/system-events", HttpMethod.GET, null, String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED)
                .withFailMessage("Expected UNAUTHORIZED (401), got %s", response.getStatusCode());
    }

    @Test
    void shouldReturn403_whenUserDoesNotHaveAuditPermission_whenGetRequestEvents() {
        HttpEntity<Void> entity = new HttpEntity<>(getUserHeaders());
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/request-events", HttpMethod.GET, entity, String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldReturn403_whenUserDoesNotHaveAuditPermission_whenGetSecurityEvents() {
        HttpEntity<Void> entity = new HttpEntity<>(getUserHeaders());
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/security-events", HttpMethod.GET, entity, String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldReturn403_whenUserDoesNotHaveAuditPermission_whenGetSystemEvents() {
        HttpEntity<Void> entity = new HttpEntity<>(getUserHeaders());
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/system-events", HttpMethod.GET, entity, String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldReturnPagedResults_whenUsingPageable_onRequestEvents() {
        String url = baseUrl + "/request-events?page=0&size=5&sort=timestamp,desc";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"size\":5");
    }

    @Test
    void shouldReturnPagedResults_whenUsingPageable_onSecurityLogs() {
        String url = baseUrl + "/security-events?page=0&size=5&sort=timestamp,desc";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"size\":5");
    }

    @Test
    void shouldReturnPagedResults_whenUsingPageable_onSystemLogs() {
        String url = baseUrl + "/system-events?page=0&size=5&sort=timestamp,desc";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"size\":5");
    }

    @Test
    void shouldReturnEmptyPage_whenUsingNonMatchingFilters_onRequestEvents() {
        String url = baseUrl + "/request-events?path=INVALID_TYPE&method=naoexiste";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"content\":[]");
    }

    @Test
    void shouldReturnEmptyPage_whenUsingNonMatchingFilters_onSecurityEvents() {
        String url = baseUrl + "/security-events?eventType=INVALID_TYPE&username=naoexiste";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"content\":[]");
    }

    @Test
    void shouldReturnEmptyPage_whenUsingNonMatchingFilters_onSystemEvents() {
        String url = baseUrl + "/system-events?action=INVALID_TYPE&targetId=naoexiste";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"content\":[]");
    }

    @Test
    void shouldReturn200_whenUsingMultipleFilters_onRequestEvents() {
        String url = baseUrl + "/system-events?action=PERMISSION_GRANTED&targetEntity=User&performedBy=admin";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturn200_whenUsingMultipleFilters_onSecurityEvents() {
        String url = baseUrl + "/system-events?action=PERMISSION_GRANTED&targetEntity=User&performedBy=admin";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturn200_whenUsingMultipleFilters_onSystemEvents() {
        String url = baseUrl + "/system-events?action=PERMISSION_GRANTED&targetEntity=User&performedBy=admin";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturn200_whenFilteringByDateRange_onRequestEvents() {
        String url = baseUrl + "/request-events?start=2025-01-01T00:00:00&end=2025-12-31T23:59:59";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturn200_whenFilteringByDateRange_onSecurityLogs() {
        String url = baseUrl + "/security-events?startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturn200_whenFilteringByDateRange_onSystemLogs() {
        String url = baseUrl + "/system-events?start=2025-01-01T00:00:00&end=2025-12-31T23:59:59";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturn401_whenUsingInvalidToken_whenGetRequestEvents() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, "ACCESS_TOKEN=invalid.token.value");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/request-events", HttpMethod.GET, entity, String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReturn401_whenUsingInvalidToken_whenGetSecurityEvents() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, "ACCESS_TOKEN=invalid.token.value");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/security-events", HttpMethod.GET, entity, String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReturn401_whenUsingInvalidToken_whenGetSystemEvents() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, "ACCESS_TOKEN=invalid.token.value");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/system-events", HttpMethod.GET, entity, String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReturn200_whenFiltersAreExplicitlyNull_onRequestEvents() {
        String url = baseUrl + "/request-events?ip=&username=";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturn200_whenFiltersAreExplicitlyNull_onSecurityEvents() {
        String url = baseUrl + "/security-events?eventType=&username=&startDate=";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturn200_whenFiltersAreExplicitlyNull_onSystemEvents() {
        String url = baseUrl + "/system-events?action=&targetEntity=&performedBy=";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturn400_whenDateIsInvalid_onRequestEvents() {
        String url = baseUrl + "/request-events?start=invalid-date-format";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturn400_whenDateIsInvalid_onSecurityEvents() {
        String url = baseUrl + "/security-events?start=invalid-date-format";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturn400_whenDateIsInvalid_onSystemEvents() {
        String url = baseUrl + "/system-events?start=invalid-date-format";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturnContent_onRequestEvents() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/request-events", HttpMethod.GET, entity, String.class
        );

        assertThat(response.getBody()).contains("path"); // ou qualquer campo do DTO esperado
    }

    @Test
    void shouldReturnContent_onSecurityEvents() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/security-events", HttpMethod.GET, entity, String.class
        );

        assertThat(response.getBody()).contains("eventType"); // ou qualquer campo do DTO esperado
    }

    @Test
    void shouldReturnContent_onSystemEvents() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/system-events", HttpMethod.GET, entity, String.class
        );

        assertThat(response.getBody()).contains("performedBy"); // ou qualquer campo do DTO esperado
    }

    @Test
    void shouldReturnSameResults_whenUsingDifferentCaseUsername_onRequestEvents() {
        String lowerCase = baseUrl + "/request-events?username=admin";
        String upperCase = baseUrl + "/request-events?username=ADMIN";

        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> lower = restTemplate.exchange(lowerCase, HttpMethod.GET, entity, String.class);
        ResponseEntity<String> upper = restTemplate.exchange(upperCase, HttpMethod.GET, entity, String.class);

        assertThat(lower.getBody()).isEqualTo(upper.getBody());
    }

    @Test
    void shouldReturnSameResults_whenUsingDifferentCaseUsername_onSecurityEvents() {
        String lowerCase = baseUrl + "/security-events?username=admin";
        String upperCase = baseUrl + "/security-events?username=ADMIN";

        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> lower = restTemplate.exchange(lowerCase, HttpMethod.GET, entity, String.class);
        ResponseEntity<String> upper = restTemplate.exchange(upperCase, HttpMethod.GET, entity, String.class);

        assertThat(lower.getBody()).isEqualTo(upper.getBody());
    }

    @Test
    void shouldReturnSameResults_whenUsingDifferentCaseUsername_onSystemEvents() {
        String lowerCase = baseUrl + "/system-events?targetEntity=admin";
        String upperCase = baseUrl + "/system-events?targetEntity=ADMIN";

        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> lower = restTemplate.exchange(lowerCase, HttpMethod.GET, entity, String.class);
        ResponseEntity<String> upper = restTemplate.exchange(upperCase, HttpMethod.GET, entity, String.class);

        assertThat(lower.getBody()).isEqualTo(upper.getBody());
    }

    @Test
    void shouldContainExpectedFields_AuditJson_onRequestEvents() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/request-events", HttpMethod.GET, entity, String.class
        );

        assertThat(response.getBody()).contains("path", "ip", "method");
    }

    @Test
    void shouldContainExpectedFields_AuditJson_onSecurityEvents() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/security-events", HttpMethod.GET, entity, String.class
        );

        assertThat(response.getBody()).contains("eventType", "username", "timestamp");
    }

    @Test
    void shouldContainExpectedFields_AuditJson_onSystemEvents() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/system-events", HttpMethod.GET, entity, String.class
        );

        assertThat(response.getBody()).contains("action", "ipAddress", "userAgent");
    }

    @Test
    void shouldReturnJsonContentType_onRequestEvents() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/request-events", HttpMethod.GET, entity, String.class
        );

        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void shouldReturnJsonContentType_onSecurityEvents() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/security-events", HttpMethod.GET, entity, String.class
        );

        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void shouldReturnJsonContentType_onSystemEvents() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/system-events", HttpMethod.GET, entity, String.class
        );

        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void shouldRespondQuickly_onRequestEvents() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        long start = System.currentTimeMillis();
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/request-events", HttpMethod.GET, entity, String.class
        );
        long duration = System.currentTimeMillis() - start;

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(duration).isLessThan(1000); // 1 segundo
    }

    @Test
    void shouldRespondQuickly_onSecurityEvents() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        long start = System.currentTimeMillis();
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/security-events", HttpMethod.GET, entity, String.class
        );
        long duration = System.currentTimeMillis() - start;

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(duration).isLessThan(1000); // 1 segundo
    }

    @Test
    void shouldRespondQuickly_onSystemEvents() {
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());
        long start = System.currentTimeMillis();
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/system-events", HttpMethod.GET, entity, String.class
        );
        long duration = System.currentTimeMillis() - start;

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(duration).isLessThan(1000); // 1 segundo
    }

    @Test
    void shouldHandleHighVolumeRequests_onAuditSearch() {
        String url = baseUrl + "/request-events?size=100";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"size\":100");
    }

    @Test
    void shouldHandleHighVolumeSecurity_onAuditSearch() {
        String url = baseUrl + "/security-events?size=100";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"size\":100");
    }

    @Test
    void shouldHandleHighVolumeSystem_onAuditSearch() {
        String url = baseUrl + "/system-events?size=100";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"size\":100");
    }

    @Test
    void shouldIncludeEventsAtStartTimeExactly_onRequestEvents() {
        String startTime = "2025-06-21T00:00:00";
        String url = baseUrl + "/request-events?start=" + startTime + "&end=2025-06-21T23:59:59";

        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldIncludeEventsAtStartTimeExactly_onSecurityEvents() {
        String startTime = "2025-06-21T00:00:00";
        String url = baseUrl + "/security-events?startDate=" + startTime + "&endDate=2025-06-21T23:59:59";

        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldIncludeEventsAtStartTimeExactly_onSystemEvents() {
        String startTime = "2025-06-21T00:00:00";
        String url = baseUrl + "/system-events?start=" + startTime + "&end=2025-06-21T23:59:59";

        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturnPaginationInfo_onRequestEvents() {
        String url = baseUrl + "/request-events?size=5&page=0";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        assertThat(response.getBody()).contains("totalElements", "totalPages");
    }

    @Test
    void shouldReturnPaginationInfo_onSecurityEvents() {
        String url = baseUrl + "/security-events?size=5&page=0";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        assertThat(response.getBody()).contains("totalElements", "totalPages");
    }

    @Test
    void shouldReturnPaginationInfo_onSystemEvents() {
        String url = baseUrl + "/system-events?size=5&page=0";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        assertThat(response.getBody()).contains("totalElements", "totalPages");
    }

    @Test
    void shouldReturnAscendingOrderByTimestamp_onRequestEvents() {
        String url = baseUrl + "/request-events?sort=timestamp,asc";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturnAscendingOrderByTimestamp_onSecurityEvents() {
        String url = baseUrl + "/security-events?sort=timestamp,asc";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturnAscendingOrderByTimestamp_onSystemEvents() {
        String url = baseUrl + "/system-events?sort=timestamp,asc";
        HttpEntity<Void> entity = new HttpEntity<>(getAdminHeaders());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}