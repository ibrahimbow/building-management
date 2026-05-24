package com.why.buildingmanagement.notification.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.why.buildingmanagement.notification.infrastructure.security.SecurityConstants.MANAGER;
import static com.why.buildingmanagement.notification.infrastructure.security.SecurityConstants.TENANT;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {

        return http.csrf(AbstractHttpConfigurer::disable)
                        .addFilterBefore(
                                        new HeaderAuthenticationFilter(),
                                        UsernamePasswordAuthenticationFilter.class)
                        .authorizeHttpRequests(auth -> auth
                                        .requestMatchers("/ws/**").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/notifications/**")
                                        .hasAnyRole(TENANT, MANAGER)
                                        .requestMatchers(HttpMethod.PATCH, "/api/notifications/**")
                                        .hasAnyRole(TENANT, MANAGER)
                                        .anyRequest().authenticated())
                        .build();
    }
}