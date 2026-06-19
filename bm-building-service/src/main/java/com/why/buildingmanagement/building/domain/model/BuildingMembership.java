package com.why.buildingmanagement.building.domain.model;

import com.why.buildingmanagement.building.domain.exception.InvalidBuildingMembershipException;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BuildingMembership {

    private final UUID id;
    private final UUID buildingId;
    private final Long tenantUserId;
    private final String tenantUsername;
    private final String tenantEmail;
    private final String tenantPhoneNumber;
    private final Instant joinedAt;
    private final Instant leftAt;

    private BuildingMembership(final UUID id,
                               final UUID buildingId,
                               final Long tenantUserId,
                               final String tenantUsername,
                               final String tenantEmail,
                               final String tenantPhoneNumber,
                               final Instant joinedAt,
                               final Instant leftAt) {

        validate(buildingId,
                 tenantUserId,
                 tenantUsername,
                 tenantEmail,
                 tenantPhoneNumber,
                 joinedAt);

        this.id = id;
        this.buildingId = buildingId;

        this.tenantUserId = tenantUserId;
        this.tenantUsername = tenantUsername.trim();
        this.tenantEmail = tenantEmail.trim();
        this.tenantPhoneNumber = tenantPhoneNumber.trim();

        this.joinedAt = joinedAt;
        this.leftAt = leftAt;
    }

    public static BuildingMembership createNew(final UUID buildingId,
                                               final Long tenantUserId,
                                               final String tenantUsername,
                                               final String tenantEmail,
                                               final String tenantPhoneNumber) {

        return new BuildingMembership(null,
                                      buildingId,
                                      tenantUserId,
                                      tenantUsername,
                                      tenantEmail,
                                      tenantPhoneNumber,
                                      Instant.now(),
                                      null);
    }

    public static BuildingMembership restore(final UUID id,
                                             final UUID buildingId,
                                             final Long tenantUserId,
                                             final String tenantUsername,
                                             final String tenantEmail,
                                             final String tenantPhoneNumber,
                                             final Instant joinedAt,
                                             final Instant leftAt) {

        if (id == null) {
            throw new InvalidBuildingMembershipException("Membership id is required when restoring membership");
        }

        return new BuildingMembership(id,
                                      buildingId,
                                      tenantUserId,
                                      tenantUsername,
                                      tenantEmail,
                                      tenantPhoneNumber,
                                      joinedAt,
                                      leftAt);
    }

    public BuildingMembership leave() {

        if (isInactive()) {
            throw new InvalidBuildingMembershipException("Tenant already left this building");
        }

        return new BuildingMembership(this.id,
                                      this.buildingId,
                                      this.tenantUserId,
                                      this.tenantUsername,
                                      this.tenantEmail,
                                      this.tenantPhoneNumber,
                                      this.joinedAt,
                                      Instant.now());
    }

    public boolean isInactive() {
        return leftAt != null;
    }

    private static void validate(final UUID buildingId,
                                 final Long tenantUserId,
                                 final String tenantUsername,
                                 final String tenantEmail,
                                 final String tenantPhoneNumber,
                                 final Instant joinedAt) {

        if (buildingId == null) {
            throw new InvalidBuildingMembershipException("Building id is required");
        }

        if (tenantUserId == null) {
            throw new InvalidBuildingMembershipException("Tenant user id is required");
        }

        requireText(tenantUsername, "Tenant username is required");
        requireText(tenantEmail, "Tenant email is required");
        requireText(tenantPhoneNumber, "Tenant phone number is required");

        if (joinedAt == null) {
            throw new InvalidBuildingMembershipException("Joined date is required");
        }
    }

    private static void requireText(final String value, final String message) {
        if (value == null || value.isBlank()) {
            throw new InvalidBuildingMembershipException(message);
        }
    }
}