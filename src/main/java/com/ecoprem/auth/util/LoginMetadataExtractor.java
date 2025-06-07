package com.ecoprem.auth.util;

import com.ecoprem.entity.auth.ActiveSession;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;

@Slf4j
@Component
public class LoginMetadataExtractor {

    private final RestTemplate restTemplate = new RestTemplate();

    public String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        return (xfHeader != null) ? xfHeader.split(",")[0] : request.getRemoteAddr();
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

    public String getLocation(String ipAddress) {
        try {
            if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
                return "Localhost (Dev)";
            }

            String url = "http://ip-api.com/json/" + ipAddress;
            GeoIpResponse response = restTemplate.getForObject(url, GeoIpResponse.class);

            if (response != null && "success".equals(response.getStatus())) {
                return response.getCity() + ", " + response.getCountry();
            }
        } catch (Exception e) {
            log.warn("🌐 Falha ao buscar localização para IP {}: {}", ipAddress, e.getMessage());
        }

        return "Unknown";
    }

    public String getHostname(String ipAddress) {
        try {
            return InetAddress.getByName(ipAddress).getHostName();
        } catch (Exception e) {
            log.warn("🔍 Falha ao resolver hostname para IP {}: {}", ipAddress, e.getMessage());
            return "Unknown";
        }
    }

    public boolean isSessionMetadataMatching(ActiveSession session, HttpServletRequest request) {
        String userAgent = getUserAgent(request);
        String currentIp = getClientIp(request);
        String currentBrowser = detectBrowser(userAgent);
        String currentOS = detectOS(userAgent);
        String currentDevice = detectDevice(userAgent);

        boolean match = session.getIpAddress().equals(currentIp)
                && session.getBrowser().equals(currentBrowser)
                && session.getOperatingSystem().equals(currentOS)
                && session.getDevice().equals(currentDevice);

        if (!match) {
            log.warn("""
                🔍 Sessão suspeita detectada:
                  → Esperado: IP={}, Browser={}, OS={}, Device={}
                  → Atual:    IP={}, Browser={}, OS={}, Device={}
                """,
                    session.getIpAddress(), session.getBrowser(), session.getOperatingSystem(), session.getDevice(),
                    currentIp, currentBrowser, currentOS, currentDevice
            );
        }

        return match;
    }
}
