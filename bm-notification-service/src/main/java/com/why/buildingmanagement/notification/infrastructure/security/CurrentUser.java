package com.why.buildingmanagement.notification.infrastructure.security;

public record CurrentUser(
                Long id,
                String email,
                String role,
                String displayName,
                String avatarUrl) {
}