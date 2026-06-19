package com.why.buildingmanagement.auth.domain.model;

import com.why.buildingmanagement.auth.domain.validation.BuildingUserValidator;
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

    private BuildingUser(final Long id,
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
        validateNewUser(username,
                        email,
                        passwordHash,
                        displayName,
                        role);

        return new BuildingUser(null,
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

    public static BuildingUser restore(final Long id,
                                       final String username,
                                       final String email,
                                       final String passwordHash,
                                       final String displayName,
                                       final String phoneNumber,
                                       final String avatarUrl,
                                       final BuildingUserRole role,
                                       final Instant createdAt,
                                       final boolean enabled) {
        return new BuildingUser(id,
                                username,
                                email,
                                passwordHash,
                                displayName,
                                phoneNumber,
                                avatarUrl,
                                role,
                                createdAt,
                                enabled);
    }

    public BuildingUser disable() {
        return new BuildingUser(id,
                                username,
                                email,
                                passwordHash,
                                displayName,
                                phoneNumber,
                                avatarUrl,
                                role,
                                createdAt,
                                false);
    }

    public BuildingUser updateProfile(final String displayName,
                                      final String phoneNumber,
                                      final String avatarUrl) {
        BuildingUserValidator.validateDisplayName(displayName);

        return new BuildingUser(id,
                                username,
                                email,
                                passwordHash,
                                displayName,
                                phoneNumber,
                                avatarUrl != null ? avatarUrl : this.avatarUrl,
                                role,
                                createdAt,
                                enabled);
    }

    public BuildingUser changePassword(final String passwordHash) {
        BuildingUserValidator.validatePasswordHash(passwordHash);

        return new BuildingUser(id,
                                username,
                                email,
                                passwordHash,
                                displayName,
                                phoneNumber,
                                avatarUrl,
                                role,
                                createdAt,
                                enabled);
    }

    private static void validateNewUser(final String username,
                                        final String email,
                                        final String passwordHash,
                                        final String displayName,
                                        final BuildingUserRole role) {
        BuildingUserValidator.validateUsername(username);
        BuildingUserValidator.validateEmail(email);
        BuildingUserValidator.validatePasswordHash(passwordHash);
        BuildingUserValidator.validateDisplayName(displayName);
        BuildingUserValidator.validateRole(role);
    }
}