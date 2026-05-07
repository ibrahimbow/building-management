package com.why.buildingmanagement.building.application.port.in;

import com.why.buildingmanagement.building.application.result.BuildingInfoResult;

import java.util.List;

public interface GetMyBuildingsUseCase {

    List<BuildingInfoResult> getMyBuildings(Long managerId);
}