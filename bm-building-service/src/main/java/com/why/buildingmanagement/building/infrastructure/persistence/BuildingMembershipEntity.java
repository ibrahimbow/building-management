package com.why.buildingmanagement.building.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "building_memberships",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_building_membership_building_tenant",
                        columnNames = {"building_id", "tenant_user_id"})}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BuildingMembershipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "building_id", nullable = false)
    private UUID buildingId;

    @Column(name = "tenant_user_id", nullable = false)
    private Long tenantUserId;

    @Column(name = "tenant_email", nullable = false)
    private String tenantEmail;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;
}