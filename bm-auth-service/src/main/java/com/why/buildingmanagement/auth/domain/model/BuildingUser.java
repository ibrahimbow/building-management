package com.why.buildingmanagement.auth.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class BuildingUser {

    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private String nickname;
    private String phoneNumber;
    private BuildingUserRole role;
    private Instant createdAt;
    private boolean enabled;

    public BuildingUser(final Long id,
                        final String username,
                        final String email,
                        final String passwordHash,
                        final String nickname,
                        final String phoneNumber,
                        final BuildingUserRole role,
                        final Instant createdAt,
                        final boolean enabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.createdAt = createdAt;
        this.enabled = enabled;
    }

    public static BuildingUser createNew(final String username,
                                         final String email,
                                         final String passwordHash,
                                         final String nickname,
                                         final String phoneNumber,
                                         final BuildingUserRole role) {

        return new BuildingUser(
                null,
                username,
                email,
                passwordHash,
                nickname,
                phoneNumber,
                role,
                Instant.now(),
                true);
    }
}