package com.why.buildingmanagement.shareandhelp.infrastructure.security;

import java.util.UUID;

public record CurrentUser(
        Long userId,
        String email,
        String role,
        String displayName,
        String avatarUrl) {
}