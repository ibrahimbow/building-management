package com.why.buildingmanagement.auth.domain.validation;

import com.why.buildingmanagement.auth.domain.exception.InvalidBuildingUserException;
import com.why.buildingmanagement.auth.domain.model.BuildingUserRole;

public final class BuildingUserValidator {

    private BuildingUserValidator() {
    }

    public static void validateUsername(final String username) {
        validateRequired(username, "Username");
    }

    public static void validateEmail(final String email) {
        validateRequired(email, "Email");
    }

    public static void validatePasswordHash(final String passwordHash) {
        validateRequired(passwordHash, "Password hash");
    }

    public static void validateDisplayName(final String displayName) {
        validateRequired(displayName, "Display name");
    }

    public static void validateRole(final BuildingUserRole role) {
        if (role == null) {
            throw new InvalidBuildingUserException("Role is required");
        }
    }

    private static void validateRequired(final String value,
                                         final String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidBuildingUserException(fieldName + " is required");
        }
    }
}