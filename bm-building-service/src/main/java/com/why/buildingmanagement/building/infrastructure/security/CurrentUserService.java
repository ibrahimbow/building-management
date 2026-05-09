package com.why.buildingmanagement.building.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final HttpServletRequest request;

    public CurrentUserService(final HttpServletRequest request) {
        this.request = request;
    }

    public CurrentUser getCurrentUser() {
        final String userId = request.getHeader("X-User-Id");
        final String email = request.getHeader("X-User-Email");
        final String role = request.getHeader("X-User-Role");
        final String username = request.getHeader("X-Username");
        final String phoneNumber = request.getHeader("X-User-Phone");

        if (userId == null || email == null || role == null || username == null) {
            throw new IllegalStateException("Missing authenticated user headers");
        }

        return new CurrentUser(
                Long.valueOf(userId),
                username,
                email,
                phoneNumber,
                role);
    }
}