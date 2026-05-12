package com.why.buildingmanagement.gateway.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class GatewaySecurityConfig {

    private static final String UNAUTHORIZED_BODY = """
            {
              "error": "Unauthorized",
              "message": "Invalid, missing, or expired JWT token"
            }
            """;


    @Bean
    public CorsWebFilter corsWebFilter() {
        final CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        final UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/api/auth/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .anyExchange().authenticated())
                .exceptionHandling(ex -> ex.authenticationEntryPoint((exchange, e) -> {
                    var response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                    return response.writeWith(Mono.just(
                            response.bufferFactory().wrap(UNAUTHORIZED_BODY.getBytes(StandardCharsets.UTF_8))
                    ));
                }))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder(JwtProperties jwtProperties) {
        String secret = jwtProperties.secret();

        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes for HS256");
        }

        return NimbusReactiveJwtDecoder
                .withSecretKey(new SecretKeySpec(
                        secret.getBytes(StandardCharsets.UTF_8),
                        "HmacSHA256"
                ))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

}