package com.why.buildingmanagement.notification.infrastructure.exception;

public class ManagerBuildingNotFoundException extends RuntimeException {

    public ManagerBuildingNotFoundException(final Long managerUserId) {
        super("No building found for manager user id: " + managerUserId);
    }
}
