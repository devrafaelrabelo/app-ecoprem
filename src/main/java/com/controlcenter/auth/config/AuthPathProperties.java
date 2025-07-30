package com.controlcenter.auth.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Data
@Configuration
@ConfigurationProperties(prefix = "auth-paths")
public class AuthPathProperties {

    @NotNull
    private List<String> publicPaths = new ArrayList<>();
}