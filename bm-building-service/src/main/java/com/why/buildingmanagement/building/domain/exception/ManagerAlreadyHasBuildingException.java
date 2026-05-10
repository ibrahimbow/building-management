package com.why.buildingmanagement.building.domain.exception;

public class ManagerAlreadyHasBuildingException extends RuntimeException {
    public ManagerAlreadyHasBuildingException(final String buildingName) {
        super("Manager already managed another building: " + buildingName);
    }
}


