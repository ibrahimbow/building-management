package com.why.buildingmanagement.auth.infrastructure.security;

import com.why.buildingmanagement.auth.application.port.out.TokenProviderPort;
import com.why.buildingmanagement.auth.domain.model.BuildingUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider implements TokenProviderPort {

    private final SecretKey secretKey;
    private final long expirationMinutes;

    public JwtTokenProvider(
            @Value("${security.jwt.secret}") final String secret,
            @Value("${security.jwt.expiration-minutes}") final long expirationMinutes) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 bytes for HS256");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    @Override
    public String generateToken(final BuildingUser buildingUser) {
        final Instant now = Instant.now();
        final Instant expiresAt = now.plusSeconds(expirationMinutes * 60);

        return Jwts.builder()
                .subject(buildingUser.getUsername())
                .claim("userId", buildingUser.getId())
                .claim("email", buildingUser.getEmail())
                .claim("nickname", buildingUser.getNickname())
                .claim("role", buildingUser.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();
    }

    public boolean isTokenValid(final String token) {
        try {
            parseClaims(token);
            return true;
        } catch (final Exception ex) {
            return false;
        }
    }

    public String getUsername(final String token) {
        return parseClaims(token).getSubject();
    }

    public Long getUserId(final String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    public String getRole(final String token) {
        return parseClaims(token).get("role", String.class);
    }

    public String getEmail(final String token) {
        return parseClaims(token).get("email", String.class);
    }

    public String getNickname(final String token) {
        return parseClaims(token).get("nickname", String.class);
    }

    private Claims parseClaims(final String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}