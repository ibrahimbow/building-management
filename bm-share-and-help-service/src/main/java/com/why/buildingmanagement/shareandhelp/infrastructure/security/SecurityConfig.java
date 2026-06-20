package com.why.buildingmanagement.shareandhelp.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                   .addFilterBefore(new HeaderAuthenticationFilter(),
                                    UsernamePasswordAuthenticationFilter.class)
                   .authorizeHttpRequests(auth -> auth
                                   .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                                   .anyRequest().permitAll())
                   .build();
    }
}