package com.why.buildingmanagement.building.application.result;

public record ManagerInfoResult(
        Long id,
        String displayName,
        String email,
        String avatarUrl) {
}