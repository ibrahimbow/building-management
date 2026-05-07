package com.why.buildingmanagement.building.application.port.in;

import com.why.buildingmanagement.building.application.result.BuildingInfoResult;

public interface UpdateMyBuildingUseCase {

    BuildingInfoResult updateMyBuilding(UpdateMyBuildingCommand command);
}