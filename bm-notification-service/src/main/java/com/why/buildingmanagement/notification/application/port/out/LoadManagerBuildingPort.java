package com.why.buildingmanagement.notification.application.port.out;

import java.util.UUID;

public interface LoadManagerBuildingPort {

    UUID loadBuildingIdByManagerUserId(Long managerUserId);
}