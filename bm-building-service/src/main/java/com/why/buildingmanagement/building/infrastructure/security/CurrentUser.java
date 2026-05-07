package com.why.buildingmanagement.building.infrastructure.security;

public record CurrentUser(Long userId,
                          String username,
                          String email,
                          String role) {
}