package com.controlcenter.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ratelimit")
public class RateLimitProperties {

    private Login login = new Login();
    private Refresh refresh = new Refresh();

    @Data
    public static class Login {
        private int ttlSeconds;
        private int maxAttemptsPerIp;
        private int maxAttemptsPerEmail;
    }

    @Data
    public static class Refresh {
        private int ttlSeconds;
        private int maxAttemptsPerIp;
    }
}
