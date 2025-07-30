package com.controlcenter.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.mail")
public class MailProperties {

    private String host;
    private int port;
    private String username;
    private String password;

    private Smtp smtp = new Smtp();

    @Getter
    @Setter
    public static class Smtp {
        private boolean auth;
        private Starttls starttls = new Starttls();

        @Getter
        @Setter
        public static class Starttls {
            private boolean enable;
        }
    }
}
