package com.controlcenter.auth.util;

import com.controlcenter.entity.auth.ActiveSession;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;

@Component
@Slf4j
public class LoginMetadataExtractor {

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserAgentAnalyzer userAgentAnalyzer;

    public LoginMetadataExtractor() {
        this.userAgentAnalyzer = UserAgentAnalyzer
                .newBuilder()
                .hideMatcherLoadStats()
                .withCache(1000)
                .build();
    }

    public String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        String ip = (xfHeader != null) ? xfHeader.split(",")[0].trim() : request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(ip)) ip = "127.0.0.1";
        return ip;
    }

    public String getUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return (userAgent != null) ? userAgent : "Unknown";
    }

    public String detectBrowser(String userAgent) {
        UserAgent agent = userAgentAnalyzer.parse(userAgent);
        return agent.getValue("AgentName");
    }

    public String detectOS(String userAgent) {
        UserAgent agent = userAgentAnalyzer.parse(userAgent);
        return agent.getValue("OperatingSystemName");
    }

    public String detectDevice(String userAgent) {
        UserAgent agent = userAgentAnalyzer.parse(userAgent);
        return agent.getValue("DeviceClass"); // Ex: Desktop, Mobile, Tablet
    }

    public String getLocation(String ipAddress) {
        try {
            if ("127.0.0.1".equals(ipAddress)) {
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