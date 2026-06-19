package com.why.buildingmanagement.auth.infrastructure.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentBuildingUserService {

    public Long getCurrentUserId() {

        final UsernamePasswordAuthenticationToken authentication = getAuthentication();

        if (authentication.getDetails() == null) {
            throw new AccessDeniedException("No authenticated user found.");
        }

        return (Long) authentication.getDetails();
    }

    public String getCurrentUsername() {
        return getAuthentication().getName();
    }

    public boolean isCurrentUserAdmin() {
        return getAuthentication().getAuthorities()
                                  .stream()
                                  .anyMatch(authority ->
                                                            "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    public void requireAdmin() {
        if (!isCurrentUserAdmin()) {
            throw new AccessDeniedException("Only ADMIN can access this resource.");
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication() {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof UsernamePasswordAuthenticationToken token)) {
            throw new AccessDeniedException("No authenticated user found.");
        }

        return token;
    }
}