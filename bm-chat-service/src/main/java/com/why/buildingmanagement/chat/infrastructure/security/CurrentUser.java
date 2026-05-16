package com.why.buildingmanagement.chat.infrastructure.security;

public record CurrentUser(
        Long userId,
        String email,
        String role,
        String displayName,
        String avatarUrl) {
}