package com.why.buildingmanagement.building.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "building_memberships")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BuildingMembershipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "building_id", nullable = false, updatable = false)
    private UUID buildingId;

    @Column(name = "tenant_user_id", nullable = false, updatable = false)
    private Long tenantUserId;

    @Column(name = "tenant_username", nullable = false)
    private String tenantUsername;

    @Column(name = "tenant_email", nullable = false)
    private String tenantEmail;

    @Column(name = "tenant_phone_number", nullable = false)
    private String tenantPhoneNumber;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    @Column(name = "left_at")
    private Instant leftAt;
}