package com.why.buildingmanagement.notification.application.port.in;

import java.util.List;
import java.util.UUID;

public interface LoadBuildingTenantUsersPort {

    List<Long> loadTenantUserIds(UUID buildingId);
}