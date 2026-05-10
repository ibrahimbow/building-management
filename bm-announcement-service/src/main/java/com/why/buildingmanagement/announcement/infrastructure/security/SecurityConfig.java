package com.why.buildingmanagement.announcement.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private static final String MANAGER = "MANAGER";
    private static final String TENANT = "TENANT";

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(new HeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/manager/announcements").hasRole(MANAGER)
                        .requestMatchers(HttpMethod.GET, "/api/manager/announcements").hasRole(MANAGER)
                        .requestMatchers(HttpMethod.PUT, "/api/manager/announcements/**").hasRole(MANAGER)
                        .requestMatchers(HttpMethod.DELETE, "/api/manager/announcements/**").hasRole(MANAGER)
                        .requestMatchers(HttpMethod.GET, "/api/tenant/announcements").hasRole(TENANT)
                        .anyRequest().authenticated())
                .build();
    }
}