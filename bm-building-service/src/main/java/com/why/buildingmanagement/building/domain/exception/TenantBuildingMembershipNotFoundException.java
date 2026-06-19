package com.why.buildingmanagement.building.domain.exception;

public class TenantBuildingMembershipNotFoundException extends RuntimeException {
    public TenantBuildingMembershipNotFoundException(final Long tenantUserId) {
        super("No active building membership found for tenant: " + tenantUserId);
    }
}
