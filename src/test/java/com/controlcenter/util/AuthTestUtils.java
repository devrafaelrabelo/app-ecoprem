package com.controlcenter.util;

import com.controlcenter.auth.dto.LoginRequest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import java.util.List;

public class AuthTestUtils {

    public static HttpHeaders getAuthenticatedHeaders(TestRestTemplate restTemplate, int port, String email, String password) {
        String loginUrl = "http://localhost:" + port + "/api/auth/login";
        LoginRequest loginRequest = new LoginRequest(email, password, false);

        ResponseEntity<Void> loginResponse = restTemplate.postForEntity(loginUrl, loginRequest, Void.class);
        List<String> cookies = loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE);

        HttpHeaders headers = new HttpHeaders();
        if (cookies != null && !cookies.isEmpty()) {
            headers.set(HttpHeaders.COOKIE, String.join("; ", cookies));
        }
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}