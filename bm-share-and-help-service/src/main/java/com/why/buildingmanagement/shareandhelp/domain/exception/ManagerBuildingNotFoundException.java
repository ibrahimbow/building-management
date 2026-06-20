package com.why.buildingmanagement.shareandhelp.domain.exception;

public class ManagerBuildingNotFoundException extends RuntimeException {
    public ManagerBuildingNotFoundException(final Long managerUserId) {
        super("No managed building found for manager user id: " + managerUserId);
    }
}
