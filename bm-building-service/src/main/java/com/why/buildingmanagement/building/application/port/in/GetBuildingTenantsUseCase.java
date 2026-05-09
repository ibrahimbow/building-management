package com.why.buildingmanagement.building.application.port.in;

import com.why.buildingmanagement.building.application.result.TenantInfoResult;

import java.util.List;
import java.util.UUID;

public interface GetBuildingTenantsUseCase {
    List<TenantInfoResult> getBuildingTenants(UUID buildingId, Long managerId);

}
