package com.controlcenter.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Data
@Configuration
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    private Durations cookiesDurations = new Durations();
    private CookieProperties cookiesProperties = new CookieProperties();
    private CookieNames cookieNames = new CookieNames();

    @Data
    public static class Durations {
        private int accessTokenMin;
        private int refreshShortMin;
        private int refreshLongMin;
        private int twofaShortMin;
    }

    @Data
    public static class CookieProperties {
        private boolean secure;
        private boolean httpOnly;
        private String sameSite;
    }

    @Data
    public static class CookieNames {
        private String access;
        private String refresh;
        private String twofa;
    }
}