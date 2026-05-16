package com.why.buildingmanagement.chat.application.port.out;

import java.util.UUID;

public interface LoadTenantBuildingPort {

    UUID loadActiveBuildingIdByTenantUserId(final Long tenantUserId);
}