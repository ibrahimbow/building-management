package com.why.buildingmanagement.building.application.port.in;

import com.why.buildingmanagement.building.application.result.BuildingInfoResult;

public interface GetMyBuildingUseCase {

    BuildingInfoResult getMyBuilding(Long tenantUserId);
}