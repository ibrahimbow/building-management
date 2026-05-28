package com.why.buildingmanagement.gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class JwtForwardingFilterTest {

    private final JwtForwardingFilter jwtForwardingFilter =
                    new JwtForwardingFilter();

    @Test
    void shouldForwardJwtClaimsAsUserHeaders() {
        final Jwt jwt = jwtWithClaims(Map.of(
                        "userId", 10L,
                        "email", "tenant@example.com",
                        "role", "tenant",
                        "displayName", "Tenant User",
                        "avatarUrl", "/api/files/PROFILE_AVATAR/avatar.png",
                        "phoneNumber", "+32470000000"));

        final ServerWebExchange exchange =
                        exchangeWithJwt(jwt);

        final AtomicReference<ServerWebExchange> capturedExchange =
                        new AtomicReference<>();

        final GatewayFilterChain chain =
                        chainCapturingExchange(capturedExchange);

        StepVerifier.create(jwtForwardingFilter.filter(exchange, chain))
                        .verifyComplete();

        final ServerWebExchange forwardedExchange =
                        capturedExchange.get();

        assertThat(forwardedExchange).isNotNull();
        assertThat(forwardedExchange.getRequest().getHeaders().getFirst("X-User-Id"))
                        .isEqualTo("10");
        assertThat(forwardedExchange.getRequest().getHeaders().getFirst("X-User-Email"))
                        .isEqualTo("tenant@example.com");
        assertThat(forwardedExchange.getRequest().getHeaders().getFirst("X-User-Role"))
                        .isEqualTo("TENANT");
        assertThat(forwardedExchange.getRequest().getHeaders().getFirst("X-Username"))
                        .isEqualTo("tenant-subject");
        assertThat(forwardedExchange.getRequest().getHeaders().getFirst("X-User-Phone"))
                        .isEqualTo("+32470000000");
        assertThat(forwardedExchange.getRequest().getHeaders().getFirst("X-User-Display-Name"))
                        .isEqualTo("Tenant User");
        assertThat(forwardedExchange.getRequest().getHeaders().getFirst("X-User-Avatar-Url"))
                        .isEqualTo("/api/files/PROFILE_AVATAR/avatar.png");
    }

    @Test
    void shouldRemoveSpoofedIncomingUserHeadersBeforeForwardingJwtClaims() {
        final Jwt jwt = jwtWithClaims(Map.of(
                        "userId", 20L,
                        "email", "manager@example.com",
                        "role", "manager",
                        "displayName", "Manager User",
                        "avatarUrl", "",
                        "phoneNumber", "+32471111111"));

        final MockServerHttpRequest request = MockServerHttpRequest
                        .get("/api/manager/buildings")
                        .header("X-User-Id", "999")
                        .header("X-User-Email", "attacker@example.com")
                        .header("X-User-Role", "ADMIN")
                        .header("X-Username", "attacker")
                        .header("X-User-Phone", "000")
                        .header("X-User-Display-Name", "Attacker")
                        .header("X-User-Avatar-Url", "/fake.png")
                        .build();

        final ServerWebExchange exchange =
                        MockServerWebExchange.from(request)
                                        .mutate()
                                        .principal(Mono.just(authentication(jwt)))
                                        .build();

        final AtomicReference<ServerWebExchange> capturedExchange =
                        new AtomicReference<>();

        StepVerifier.create(jwtForwardingFilter.filter(
                                        exchange,
                                        chainCapturingExchange(capturedExchange)))
                        .verifyComplete();

        final ServerWebExchange forwardedExchange =
                        capturedExchange.get();

        assertThat(forwardedExchange.getRequest().getHeaders().getFirst("X-User-Id"))
                        .isEqualTo("20");
        assertThat(forwardedExchange.getRequest().getHeaders().getFirst("X-User-Email"))
                        .isEqualTo("manager@example.com");
        assertThat(forwardedExchange.getRequest().getHeaders().getFirst("X-User-Role"))
                        .isEqualTo("MANAGER");
        assertThat(forwardedExchange.getRequest().getHeaders().getFirst("X-Username"))
                        .isEqualTo("tenant-subject");
        assertThat(forwardedExchange.getRequest().getHeaders().getFirst("X-User-Phone"))
                        .isEqualTo("+32471111111");
        assertThat(forwardedExchange.getRequest().getHeaders().getFirst("X-User-Display-Name"))
                        .isEqualTo("Manager User");
        assertThat(forwardedExchange.getRequest().getHeaders().getFirst("X-User-Avatar-Url"))
                        .isEqualTo("");
    }

    @Test
    void shouldUseSubjectAsDisplayNameWhenDisplayNameClaimIsMissing() {
        final Jwt jwt = jwtWithClaims(Map.of(
                        "userId", 10L,
                        "email", "tenant@example.com",
                        "role", "tenant"));

        final AtomicReference<ServerWebExchange> capturedExchange =
                        new AtomicReference<>();

        StepVerifier.create(jwtForwardingFilter.filter(
                                        exchangeWithJwt(jwt),
                                        chainCapturingExchange(capturedExchange)))
                        .verifyComplete();

        assertThat(capturedExchange.get().getRequest().getHeaders()
                        .getFirst("X-User-Display-Name"))
                        .isEqualTo("tenant-subject");

        assertThat(capturedExchange.get().getRequest().getHeaders()
                        .getFirst("X-User-Avatar-Url"))
                        .isEqualTo("");
    }

    @Test
    void shouldContinueWithoutForwardingHeadersWhenPrincipalIsMissing() {
        final ServerWebExchange exchange =
                        MockServerWebExchange.from(
                                        MockServerHttpRequest.get("/api/notifications").build());

        final AtomicReference<ServerWebExchange> capturedExchange =
                        new AtomicReference<>();

        StepVerifier.create(jwtForwardingFilter.filter(
                                        exchange,
                                        chainCapturingExchange(capturedExchange)))
                        .verifyComplete();

        assertThat(capturedExchange.get()).isSameAs(exchange);
        assertThat(capturedExchange.get().getRequest().getHeaders()
                        .containsKey("X-User-Id"))
                        .isFalse();
    }

    @Test
    void shouldContinueWithoutForwardingHeadersWhenPrincipalIsNotJwt() {
        final TestingAuthenticationToken authentication =
                        new TestingAuthenticationToken("user", "password");

        final ServerWebExchange exchange =
                        MockServerWebExchange.from(
                                                        MockServerHttpRequest.get("/api/notifications").build())
                                        .mutate()
                                        .principal(Mono.just(authentication))
                                        .build();

        final AtomicReference<ServerWebExchange> capturedExchange =
                        new AtomicReference<>();

        StepVerifier.create(jwtForwardingFilter.filter(
                                        exchange,
                                        chainCapturingExchange(capturedExchange)))
                        .verifyComplete();

        assertThat(capturedExchange.get()).isSameAs(exchange);
        assertThat(capturedExchange.get().getRequest().getHeaders()
                        .containsKey("X-User-Id"))
                        .isFalse();
    }

    @Test
    void shouldHaveExpectedOrder() {
        assertThat(jwtForwardingFilter.getOrder()).isEqualTo(1);
    }

    private static ServerWebExchange exchangeWithJwt(final Jwt jwt) {
        return MockServerWebExchange.from(
                                        MockServerHttpRequest.get("/api/notifications").build())
                        .mutate()
                        .principal(Mono.just(authentication(jwt)))
                        .build();
    }

    private static TestingAuthenticationToken authentication(final Jwt jwt) {
        return new TestingAuthenticationToken(jwt, null);
    }

    private static GatewayFilterChain chainCapturingExchange(
                    final AtomicReference<ServerWebExchange> capturedExchange) {

        return exchange -> {
            capturedExchange.set(exchange);
            return Mono.empty();
        };
    }

    private static Jwt jwtWithClaims(final Map<String, Object> claims) {
        return Jwt.withTokenValue("token")
                        .header("alg", "HS256")
                        .subject("tenant-subject")
                        .issuedAt(Instant.parse("2026-05-29T10:00:00Z"))
                        .expiresAt(Instant.parse("2026-05-29T11:00:00Z"))
                        .claims(jwtClaims -> jwtClaims.putAll(claims))
                        .build();
    }
}