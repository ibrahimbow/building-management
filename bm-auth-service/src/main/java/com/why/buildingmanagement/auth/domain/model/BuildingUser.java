package com.why.buildingmanagement.auth.domain.model;

import lombok.Getter;

import java.time.Instant;

@Getter
public class BuildingUser {

    private final Long id;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final String displayName;
    private final String phoneNumber;
    private final String avatarUrl;
    private final BuildingUserRole role;
    private final Instant createdAt;
    private final boolean enabled;

    public BuildingUser(final Long id,
                        final String username,
                        final String email,
                        final String passwordHash,
                        final String displayName,
                        final String phoneNumber,
                        final String avatarUrl,
                        final BuildingUserRole role,
                        final Instant createdAt,
                        final boolean enabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        this.avatarUrl = avatarUrl;
        this.role = role;
        this.createdAt = createdAt;
        this.enabled = enabled;
    }

    public static BuildingUser createNew(final String username,
                                         final String email,
                                         final String passwordHash,
                                         final String displayName,
                                         final String phoneNumber,
                                         final BuildingUserRole role) {

        return new BuildingUser(
                null,
                username,
                email,
                passwordHash,
                displayName,
                phoneNumber,
                null,
                role,
                Instant.now(),
                true);
    }
}