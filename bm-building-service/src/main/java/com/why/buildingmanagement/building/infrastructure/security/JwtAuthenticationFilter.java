package com.why.buildingmanagement.building.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;

    public JwtAuthenticationFilter(final JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                    @NonNull final HttpServletResponse response,
                                    @NonNull final FilterChain filterChain) throws ServletException, IOException {

        final String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = header.substring(7);

        try {
            final Claims claims = Jwts.parser()
                                      .verifyWith(Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8)))
                                      .build()
                                      .parseSignedClaims(token)
                                      .getPayload();

            final String userId = String.valueOf(claims.get("userId"));
            final String email = claims.get("email", String.class);
            final String username = claims.getSubject();
            final List<String> roles = extractRoles(claims);

            final List<SimpleGrantedAuthority> authorities =
                            roles.stream()
                                 .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                                 .map(SimpleGrantedAuthority::new)
                                 .toList();

            final AuthenticatedUser principal = new AuthenticatedUser(userId, email, username, roles);

            final UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(principal, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private List<String> extractRoles(final Claims claims) {
        final List<?> rawRoles = claims.get("roles", List.class);

        if (rawRoles != null && !rawRoles.isEmpty()) {
            return rawRoles.stream()
                           .map(Object::toString)
                           .toList();
        }

        final String singleRole = claims.get("role", String.class);

        if (singleRole == null || singleRole.isBlank()) {
            return List.of();
        }

        return List.of(singleRole);
    }
}