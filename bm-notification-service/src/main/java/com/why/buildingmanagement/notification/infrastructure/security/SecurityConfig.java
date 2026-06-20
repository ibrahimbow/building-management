package com.why.buildingmanagement.notification.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.why.buildingmanagement.notification.infrastructure.security.SecurityConstants.*;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {

        return http.csrf(AbstractHttpConfigurer::disable)
                   .addFilterBefore(new HeaderAuthenticationFilter(),
                                    UsernamePasswordAuthenticationFilter.class)
                   .authorizeHttpRequests(auth -> auth
                                   .requestMatchers("/ws/**").permitAll()

                                   .requestMatchers(HttpMethod.GET,
                                                    "/api/notifications",
                                                    "/api/notifications/**",
                                                    "/notifications",
                                                    "/notifications/**")
                                   .hasAnyRole(TENANT, MANAGER, ADMIN)

                                   .requestMatchers(HttpMethod.PATCH,
                                                    "/api/notifications/**",
                                                    "/notifications/**")
                                   .hasAnyRole(TENANT, MANAGER, ADMIN)

                                   .anyRequest().authenticated())
                   .build();
    }
}