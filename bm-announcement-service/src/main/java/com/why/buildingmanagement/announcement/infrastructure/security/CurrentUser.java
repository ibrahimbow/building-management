package com.why.buildingmanagement.announcement.infrastructure.security;

public record CurrentUser(
        Long userId,
        String username,
        String email,
        String phoneNumber,
        String role) {
}