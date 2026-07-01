package com.why.buildingmanagement.chat.infrastructure.security;

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

        return http.cors(AbstractHttpConfigurer::disable)
                   .csrf(AbstractHttpConfigurer::disable)
                   .addFilterBefore(new HeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                   .authorizeHttpRequests(auth -> auth
                                   .requestMatchers(
                                                   "/actuator/health",
                                                   "/actuator/info",
                                                   "/ws/**")
                                   .permitAll()
                                   .anyRequest().permitAll())
                   .build();
    }
}