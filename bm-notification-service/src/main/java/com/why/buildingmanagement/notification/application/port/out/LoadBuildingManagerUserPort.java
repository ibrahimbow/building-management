package com.why.buildingmanagement.notification.application.port.out;

import java.util.UUID;

public interface LoadBuildingManagerUserPort {

    Long loadManagerUserIdByBuildingId(final UUID buildingId);
}