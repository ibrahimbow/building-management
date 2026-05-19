package com.why.buildingmanagement.chat.domain.exception;

public class ManagerBuildingNotFoundException extends RuntimeException {
    public ManagerBuildingNotFoundException(final Long managerUserId) {
        super("No building found for manager id: " + managerUserId);
    }
}
