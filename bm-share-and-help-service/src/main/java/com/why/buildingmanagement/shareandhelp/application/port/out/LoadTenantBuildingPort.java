package com.why.buildingmanagement.shareandhelp.application.port.out;

import java.util.UUID;

public interface LoadTenantBuildingPort {

    UUID loadActiveBuildingIdByTenantUserId(final Long tenantUserId);
}