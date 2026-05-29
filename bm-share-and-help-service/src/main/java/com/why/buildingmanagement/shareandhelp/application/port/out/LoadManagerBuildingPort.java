package com.why.buildingmanagement.shareandhelp.application.port.out;

import java.util.UUID;

public interface LoadManagerBuildingPort {

    UUID loadManagedBuildingIdByManagerUserId(Long managerUserId);
}
