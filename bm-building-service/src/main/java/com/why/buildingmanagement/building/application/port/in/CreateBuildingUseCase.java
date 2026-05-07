package com.why.buildingmanagement.building.application.port.in;

import com.why.buildingmanagement.building.application.result.BuildingInfoResult;

import java.util.List;

public interface CreateBuildingUseCase {

    BuildingInfoResult createBuilding(CreateBuildingCommand command);

    List<BuildingInfoResult> findByManagerId(Long managerId);
}