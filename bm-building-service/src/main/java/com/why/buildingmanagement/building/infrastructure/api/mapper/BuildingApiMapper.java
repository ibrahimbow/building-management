package com.why.buildingmanagement.building.infrastructure.api.mapper;

import com.why.buildingmanagement.building.application.result.BuildingInfoResult;
import com.why.buildingmanagement.building.application.result.TenantInfoResult;
import com.why.buildingmanagement.building.infrastructure.api.dto.response.BuildingResponse;
import com.why.buildingmanagement.building.infrastructure.api.dto.response.TenantInfoResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BuildingApiMapper {

    BuildingResponse toResponse(BuildingInfoResult result);

    TenantInfoResponse toResponse(TenantInfoResult result);
}