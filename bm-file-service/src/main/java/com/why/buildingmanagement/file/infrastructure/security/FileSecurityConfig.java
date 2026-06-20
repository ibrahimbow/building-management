package com.why.buildingmanagement.file.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class FileSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain fileSecurityFilterChain(final HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/files/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/files/upload").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().permitAll())
                .build();
    }
}