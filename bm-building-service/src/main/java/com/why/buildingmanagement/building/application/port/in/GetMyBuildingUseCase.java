package com.why.buildingmanagement.building.application.port.in;

import com.why.buildingmanagement.building.application.result.BuildingInfoResult;

import java.util.UUID;

public interface GetMyJoinedBuildingByIdUseCase {

    BuildingInfoResult getMyJoinedBuildingById(UUID buildingId, Long tenantUserId);
}