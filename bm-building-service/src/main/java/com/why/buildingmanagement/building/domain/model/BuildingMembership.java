package com.why.buildingmanagement.building.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class BuildingMembership {

    private UUID id;
    private UUID buildingId;
    private Long tenantUserId;
    private String tenantEmail;
    private Instant joinedAt;

    public BuildingMembership(
            final UUID id,
            final UUID buildingId,
            final Long tenantUserId,
            final String tenantEmail,
            final Instant joinedAt) {
        this.id = id;
        this.buildingId = buildingId;
        this.tenantUserId = tenantUserId;
        this.tenantEmail = tenantEmail;
        this.joinedAt = joinedAt;
    }

    public static BuildingMembership createNew(
            final UUID buildingId,
            final Long tenantUserId,
            final String tenantEmail) {
        return new BuildingMembership(
                null,
                buildingId,
                tenantUserId,
                tenantEmail,
                Instant.now());
    }
}