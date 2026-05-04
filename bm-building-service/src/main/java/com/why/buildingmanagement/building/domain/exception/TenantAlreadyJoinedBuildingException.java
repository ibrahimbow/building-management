package com.why.buildingmanagement.building.domain.exception;

import java.util.UUID;

public class TenantAlreadyJoinedBuildingException extends RuntimeException {
    public TenantAlreadyJoinedBuildingException(final UUID buildingId, final Long tenantUserId) {
        super("Tenant " + tenantUserId + " already joined building " + buildingId);
    }
}
