package com.why.buildingmanagement.auth.application.result;

import com.why.buildingmanagement.auth.domain.model.BuildingUserRole;

import java.time.Instant;

public record AdminBuildingUserResult(
                Long id,
                String username,
                String email,
                String displayName,
                String phoneNumber,
                String avatarUrl,
                BuildingUserRole role,
                Instant createdAt,
                boolean enabled) {
}