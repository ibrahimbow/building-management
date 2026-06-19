package com.why.buildingmanagement.building.application.port.out;

import com.why.buildingmanagement.building.application.result.BuildingInfoResult;
import com.why.buildingmanagement.building.domain.model.Building;

public interface BuildingResultMapperPort {

    BuildingInfoResult toResult(Building building);

}
