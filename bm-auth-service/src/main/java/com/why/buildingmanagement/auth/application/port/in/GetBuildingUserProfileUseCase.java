package com.why.buildingmanagement.auth.application.port.in;

import com.why.buildingmanagement.auth.application.result.BuildingUserProfileResult;

public interface GetBuildingUserProfileUseCase {

    BuildingUserProfileResult getProfile(Long userId);
}