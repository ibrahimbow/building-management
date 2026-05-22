package com.why.buildingmanagement.auth.infrastructure.api.dto.response;

public record CurrentBuildingUserResponse(
        Long id,
        String username,
        String email,
        String displayName,
        String phoneNumber,
        String avatarUrl,
        String preferredLanguage,
        Boolean notificationsEnabled,
        String role) {
}