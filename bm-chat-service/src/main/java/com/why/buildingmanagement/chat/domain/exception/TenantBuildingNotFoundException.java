package com.why.buildingmanagement.chat.domain.exception;

public class TenantBuildingNotFoundException extends RuntimeException {
    public TenantBuildingNotFoundException(final Long tenantUserId) {
        super("No active building found for Tenant user id: " + tenantUserId);
    }

}
