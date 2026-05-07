package com.why.buildingmanagement.building.domain.exception;

import java.util.UUID;

public class BuildingNotFoundException extends RuntimeException {
    public BuildingNotFoundException(final String code) {
        super("Building not found with code: " + code);
    }

    public BuildingNotFoundException(UUID buildingId) {
        super("Building not found: " + buildingId);
    }
}
