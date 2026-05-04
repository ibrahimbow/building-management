package com.why.buildingmanagement.building.domain.exception;

public class BuildingNotFoundException extends RuntimeException {
    public BuildingNotFoundException(final String code) {
        super("Building not found with code: " + code);
    }
}
