package com.why.buildingmanagement.building.application.assembler;

import com.why.buildingmanagement.building.application.port.out.LoadManagerInfoPort;
import com.why.buildingmanagement.building.application.result.BuildingInfoResult;
import com.why.buildingmanagement.building.application.result.ManagerInfoResult;
import com.why.buildingmanagement.building.domain.model.Building;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuildingInfoAssembler {

    private final LoadManagerInfoPort loadManagerInfoPort;

    public BuildingInfoResult toResult(final Building building) {
        final ManagerInfoResult managerInfo = loadManagerInfoPort.loadManagerInfoById(building.getManagerId());

        return new BuildingInfoResult(building.getId().toString(),
                                      building.getBuildingName(),
                                      building.getCode(),
                                      building.getAddress(),
                                      building.getManagerId(),
                                      managerInfo.displayName(),
                                      building.getTotalApartments(),
                                      building.getEmergencyPhone());
    }
}
