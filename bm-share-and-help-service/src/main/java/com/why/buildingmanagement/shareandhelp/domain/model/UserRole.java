package com.why.buildingmanagement.shareandhelp.domain.model;

public enum UserRole {

    ADMIN,
    MANAGER,
    TENANT;

    public boolean usesManagedBuilding() {
        return this == ADMIN || this == MANAGER;
    }

    public static UserRole from(final String role) {
        return UserRole.valueOf(role.toUpperCase());
    }
}