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
    private String tenantUsername;
    private String tenantEmail;
    private String tenantPhoneNumber;

    private Instant joinedAt;
    private Instant leftAt;

    public BuildingMembership(final UUID id,
                              final UUID buildingId,
                              final Long tenantUserId,
                              final String tenantUsername,
                              final String tenantEmail,
                              final String tenantPhoneNumber,
                              final Instant joinedAt,
                              final Instant leftAt) {
        this.id = id;
        this.buildingId = buildingId;
        this.tenantUserId = tenantUserId;
        this.tenantUsername = tenantUsername;
        this.tenantEmail = tenantEmail;
        this.tenantPhoneNumber = tenantPhoneNumber;
        this.joinedAt = joinedAt;
        this.leftAt = leftAt;
    }

    public static BuildingMembership createNew(final UUID buildingId,
                                               final Long tenantUserId,
                                               final String tenantUsername,
                                               final String tenantEmail,
                                               final String tenantPhoneNumber) {
        return new BuildingMembership(
                null,
                buildingId,
                tenantUserId,
                tenantUsername,
                tenantEmail,
                tenantPhoneNumber,
                Instant.now(),
                null
        );
    }

    public static BuildingMembership restore(final UUID id,
                                             final UUID buildingId,
                                             final Long tenantUserId,
                                             final String tenantUsername,
                                             final String tenantEmail,
                                             final String tenantPhoneNumber,
                                             final Instant joinedAt,
                                             final Instant leftAt) {
        return new BuildingMembership(
                id,
                buildingId,
                tenantUserId,
                tenantUsername,
                tenantEmail,
                tenantPhoneNumber,
                joinedAt,
                leftAt);
    }

    public void leave() {
        if (isInactive()) {
            throw new IllegalStateException("Tenant already left this building");
        }

        this.leftAt = Instant.now();
    }

    public boolean isActive() {
        return leftAt == null;
    }

    public boolean isInactive() {
        return leftAt != null;
    }
}