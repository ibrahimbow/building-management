package com.why.buildingmanagement.auth.infrastructure.security;

import com.why.buildingmanagement.auth.application.port.out.TokenProviderPort;
import com.why.buildingmanagement.auth.domain.model.BuildingUser;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@Component
public class FakeTokenProvider implements TokenProviderPort {

    @Override
    public String generateToken(BuildingUser buildingUser) {
        return "FAKE_TOKEN_FOR_" + buildingUser.getUsername();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/hello",
                                "/api/auth/register",
                                "/api/auth/login"
                        ).permitAll()
                        .anyRequest().authenticated()
                );
        // + your JWT filter etc.

        return http.build();
    }
}
