package com.why.buildingmanagement.building.application.result;

public record TenantInfoResult(
        Long tenantUserId,
        String username,
        String email,
        String phoneNumber) {
}
