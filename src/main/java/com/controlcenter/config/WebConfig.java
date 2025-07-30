package com.controlcenter.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = corsProperties.getAllowedOrigins() != null ? corsProperties.getAllowedOrigins() : new String[]{"http://localhost:3000"};

        String[] methods = corsProperties.getAllowedMethods() != null ? corsProperties.getAllowedMethods() : new String[]{"GET", "POST", "PUT", "DELETE"};

        registry.addMapping("/**")
                .allowedOriginPatterns(origins)  // 👉 trocou para allowedOriginPatterns
                .allowedMethods(methods)
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
