package com.why.buildingmanagement.audit.infrastructure.security;

public record CurrentUser(Long userId,
                          String email,
                          String role,
                          String displayName,
                          String avatarUrl) {
}