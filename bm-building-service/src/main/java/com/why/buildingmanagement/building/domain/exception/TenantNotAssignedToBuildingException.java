package com.why.buildingmanagement.building.domain.exception;

public class TenantNotAssignedToBuildingException extends RuntimeException {

    public TenantNotAssignedToBuildingException(final Long tenantUserId) {
        super("Tenant Not assigned to building: " + tenantUserId);
    }
}