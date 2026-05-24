package com.why.buildingmanagement.notification.application.port.out;

import java.util.UUID;

public interface LoadTenantBuildingPort {

    UUID loadActiveBuildingIdByTenantUserId(Long tenantUserId);
}