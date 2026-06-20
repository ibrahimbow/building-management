package com.why.buildingmanagement.shareandhelp.domain.exception;

public class TenantBuildingNotFoundException extends RuntimeException {
    public TenantBuildingNotFoundException(final Long tenantUserId) {
        super("No active building found for tenant user id: " + tenantUserId);
    }
}
