package com.controlcenter.config;


import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {
    private String[] allowedOrigins;
    private String[] allowedMethods;
}
