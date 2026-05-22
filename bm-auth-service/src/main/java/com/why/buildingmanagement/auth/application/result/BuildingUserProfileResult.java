package com.why.buildingmanagement.auth.application.result;

public record BuildingUserProfileResult(
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