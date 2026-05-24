package com.why.buildingmanagement.notification.application.port.out;

import java.util.List;
import java.util.UUID;

public interface LoadBuildingTenantUsersPort {

    List<Long> loadTenantUserIds(UUID buildingId);
}