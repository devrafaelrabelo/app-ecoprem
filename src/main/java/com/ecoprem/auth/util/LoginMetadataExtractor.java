package com.ecoprem.auth.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LoginMetadataExtractor {

    private final RestTemplate restTemplate = new RestTemplate();

    public String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    public String getUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return (userAgent != null) ? userAgent : "Unknown";
    }

    public String detectBrowser(String userAgent) {
        if (userAgent.contains("Chrome")) return "Chrome";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) return "Safari";
        if (userAgent.contains("Edge")) return "Edge";
        return "Other";
    }

    public String detectOS(String userAgent) {
        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Mac")) return "Mac";
        if (userAgent.contains("X11")) return "Unix";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iPhone")) return "iOS";
        return "Other";
    }

    public String detectDevice(String userAgent) {
        if (userAgent.contains("Mobi")) return "Mobile";
        if (userAgent.contains("Tablet")) return "Tablet";
        return "Desktop";
    }

    // NOVO: Busca localização real, ou fake se falhar
    public String getLocation(String ipAddress) {
        try {
            // Ignorar localhost/dev
            if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
                return "Localhost (Dev)";
            }

            String url = "http://ip-api.com/json/" + ipAddress;
            GeoIpResponse response = restTemplate.getForObject(url, GeoIpResponse.class);

            if (response != null && "success".equals(response.getStatus())) {
                return response.getCity() + ", " + response.getCountry();
            } else {
                return "Unknown";
            }
        } catch (Exception e) {
            System.out.println("Failed to get location for IP " + ipAddress);
            return "Unknown";
        }
    }
}
