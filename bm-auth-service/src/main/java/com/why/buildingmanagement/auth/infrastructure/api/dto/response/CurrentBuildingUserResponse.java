package com.why.buildingmanagement.auth.infrastructure.api.dto.response;

public record CurrentBuildingUserResponse(Long id,
                                          String username,
                                          String email,
                                          String role) {
}
