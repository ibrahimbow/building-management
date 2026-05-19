package com.why.buildingmanagement.chat.application.port.out;

import java.util.UUID;

public interface LoadManagerBuildingPort {

    UUID loadBuildingIdByManagerUserId(Long managerUserId);

}
