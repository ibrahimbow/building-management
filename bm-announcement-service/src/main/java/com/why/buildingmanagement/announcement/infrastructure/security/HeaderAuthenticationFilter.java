package com.why.buildingmanagement.announcement.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                    @NotNull final HttpServletResponse response,
                                    @NonNull final FilterChain filterChain)
            throws ServletException, IOException {

        final String userId = request.getHeader("X-User-Id");
        final String username = request.getHeader("X-Username");
        final String email = request.getHeader("X-User-Email");
        final String phoneNumber = request.getHeader("X-User-Phone");
        final String role = request.getHeader("X-User-Role");

        if (userId != null && username != null && email != null && role != null) {
            final CurrentUser currentUser = new CurrentUser(
                    Long.valueOf(userId),
                    username,
                    email,
                    phoneNumber,
                    role);

            final UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            currentUser,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}