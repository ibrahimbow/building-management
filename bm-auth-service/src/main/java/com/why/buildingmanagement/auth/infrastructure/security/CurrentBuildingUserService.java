package com.why.buildingmanagement.auth.infrastructure.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentBuildingUserService {

    public Long getCurrentUserId() {

        final UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext()
                                .getAuthentication();

        if (authentication == null || authentication.getDetails() == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        return (Long) authentication.getDetails();
    }

    public String getCurrentUsername() {

        final UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext()
                                .getAuthentication();

        if (authentication == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        return authentication.getName();
    }
}