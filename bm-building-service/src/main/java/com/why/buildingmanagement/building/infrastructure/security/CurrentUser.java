package com.why.buildingmanagement.building.infrastructure.security;

public record CurrentUser(
                Long userId,
                String email,
                String role,
                String displayName,
                String avatarUrl,
                String phoneNumber) {
}