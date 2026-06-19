package com.why.buildingmanagement.building.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient authServiceRestClient(@Value("${services.auth-service.url}") final String authServiceUrl) {

        return RestClient.builder()
                         .baseUrl(authServiceUrl)
                         .build();
    }
}