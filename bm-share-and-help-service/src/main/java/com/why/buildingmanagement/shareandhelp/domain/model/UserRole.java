package com.why.buildingmanagement.shareandhelp.domain.model;

import com.why.buildingmanagement.shareandhelp.domain.exception.InvalidUserRoleException;

public enum UserRole {

    ADMIN,
    MANAGER,
    TENANT;

    public boolean usesManagedBuilding() {
        return this == ADMIN || this == MANAGER;
    }

    public static UserRole from(final String role) {

        if (role == null || role.isBlank()) {
            throw new InvalidUserRoleException(role);
        }

        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (final IllegalArgumentException ex) {
            throw new InvalidUserRoleException(role);
        }
    }
}