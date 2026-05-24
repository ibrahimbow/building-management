package com.why.buildingmanagement.announcement.infrastructure.security;

public record CurrentUser(
                Long userId,
                String email,
                String role,
                String displayName,
                String avatarUrl) {
}