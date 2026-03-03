package com.frauddetection.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configures a WebClient bean pointing at the ML Service.
 * Used in Sprint 3 to POST transaction features for scoring.
 */
@Configuration
public class WebClientConfig {

    @Value("${ml-service.base-url:http://localhost:8082}")
    private String mlServiceBaseUrl;

    @Bean
    public WebClient mlServiceWebClient() {
        return WebClient.builder()
                .baseUrl(mlServiceBaseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
