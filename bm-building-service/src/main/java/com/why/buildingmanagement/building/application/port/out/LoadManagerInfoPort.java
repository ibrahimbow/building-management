package com.why.buildingmanagement.building.application.port.out;

import com.why.buildingmanagement.building.application.result.ManagerInfoResult;

public interface LoadManagerInfoPort {

    ManagerInfoResult loadManagerInfoById(Long managerId);
}