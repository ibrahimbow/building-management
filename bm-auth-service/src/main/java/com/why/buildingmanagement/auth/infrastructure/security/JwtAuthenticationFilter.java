package com.why.buildingmanagement.auth.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(final JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                    @NonNull final HttpServletResponse response,
                                    @NonNull final FilterChain filterChain)
                    throws ServletException, IOException {

        final String path = request.getServletPath();

        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authorizationHeader.substring(7);

        if (!jwtTokenProvider.isTokenValid(token)) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }

        final String username = jwtTokenProvider.getUsername(token);

        final String role = jwtTokenProvider.getRole(token);

        final Long userId = jwtTokenProvider.getUserId(token);

        final UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username,
                                                                null,
                                                                List.of(new SimpleGrantedAuthority("ROLE_" + role)));

        authentication.setDetails(userId);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(final String path) {

        return path.startsWith("/actuator/")
                        || path.equals("/api/auth/welcome")
                        || path.equals("/api/auth/register")
                        || path.equals("/api/auth/login")
                        || path.equals("/api/auth/refresh")
                        || path.equals("/api/auth/logout");
    }
}