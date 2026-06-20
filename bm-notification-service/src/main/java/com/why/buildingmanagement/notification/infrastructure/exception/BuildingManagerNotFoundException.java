package com.why.buildingmanagement.notification.infrastructure.exception;

import java.util.UUID;

public class BuildingManagerNotFoundException extends RuntimeException {
    public BuildingManagerNotFoundException(final UUID buildingId) {
        super("No manager found for building: " + buildingId);
    }
}
