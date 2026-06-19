package com.why.buildingmanagement.auth.infrastructure.api.mapper;

import com.why.buildingmanagement.auth.application.result.BuildingUserProfileResult;
import com.why.buildingmanagement.auth.infrastructure.api.dto.response.BuildingUserProfileResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BuildingUserProfileResponseMapper {

    default BuildingUserProfileResponse toResponse(
                    final BuildingUserProfileResult result) {

        return new BuildingUserProfileResponse(result.id(),
                                               result.username(),
                                               result.email(),
                                               result.displayName(),
                                               result.phoneNumber(),
                                               result.avatarUrl(),
                                               result.preferredLanguage(),
                                               result.notificationsEnabled(),
                                               result.role());
    }
}