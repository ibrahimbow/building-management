package com.why.buildingmanagement.announcement.infrastructure.security;

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
        final String username = request.getHeader("X-Username");
        final String email = request.getHeader("X-User-Email");
        final String phoneNumber = request.getHeader("X-User-Phone");
        final String role = request.getHeader("X-User-Role");

        if (userId == null || username == null || email == null || role == null) {
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