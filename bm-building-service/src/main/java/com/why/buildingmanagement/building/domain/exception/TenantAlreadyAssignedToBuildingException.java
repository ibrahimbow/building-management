package com.why.buildingmanagement.building.domain.exception;

public class TenantAlreadyAssignedToBuildingException extends RuntimeException {

    public TenantAlreadyAssignedToBuildingException(final Long tenantUserId) {
        super("Tenant already assigned to another building: " + tenantUserId);
    }
}