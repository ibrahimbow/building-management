package com.why.buildingmanagement.building.infrastructure.api.dto.response;

public record TenantInfoResponse(
        Long tenantUserId,
        String username,
        String email,
        String phoneNumber) {
}