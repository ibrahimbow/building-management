package com.why.buildingmanagement.announcement.application.port.out;

import java.util.UUID;

public interface BuildingAccessPort {

    UUID getManagerBuildingId(Long managerId);

    UUID getTenantActiveBuildingId(Long tenantUserId);
}