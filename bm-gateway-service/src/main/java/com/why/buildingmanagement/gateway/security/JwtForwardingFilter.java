package com.why.buildingmanagement.gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtForwardingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {

        return exchange.getPrincipal()
                .cast(Authentication.class)
                .filter(authentication -> authentication.getPrincipal() instanceof Jwt)
                .map(authentication -> (Jwt) authentication.getPrincipal())
                .flatMap(jwt -> {

                    final String role = jwt.getClaimAsString("role");
                    final Object userId = jwt.getClaim("userId");

                    final String displayName = jwt.getClaimAsString("displayName");
                    final String avatarUrl = jwt.getClaimAsString("avatarUrl");

                    final ServerHttpRequest request = exchange.getRequest()
                            .mutate()
                            .headers(headers -> {
                                headers.remove("X-User-Id");
                                headers.remove("X-User-Email");
                                headers.remove("X-User-Role");
                                headers.remove("X-Username");
                                headers.remove("X-User-Phone");
                                headers.remove("X-User-Display-Name");
                                headers.remove("X-User-Avatar-Url");
                            })
                            .header("X-User-Id", String.valueOf(userId))
                            .header("X-User-Email", jwt.getClaimAsString("email"))
                            .header("X-User-Role", role == null ? "" : role.toUpperCase())
                            .header("X-Username", jwt.getSubject())
                            .header("X-User-Phone", jwt.getClaimAsString("phoneNumber"))
                            .header("X-User-Display-Name", displayName == null ? jwt.getSubject() : displayName)
                            .header("X-User-Avatar-Url", avatarUrl == null ? "" : avatarUrl)
                            .build();

                    return chain.filter(exchange.mutate().request(request).build());
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {

        return 1;
    }
}