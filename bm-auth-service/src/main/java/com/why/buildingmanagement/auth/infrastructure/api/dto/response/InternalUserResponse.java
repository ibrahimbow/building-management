package com.why.buildingmanagement.auth.infrastructure.api.dto.response;

public record InternalUserResponse(Long id,
                                   String displayName,
                                   String email,
                                   String avatarUrl) {
}