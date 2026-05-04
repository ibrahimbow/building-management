package com.why.buildingmanagement.building.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
        name = "buildings",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_buildings_code", columnNames = "building_code")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BuildingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "building_name", nullable = false)
    private String buildingName;

    @Column(name = "building_code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false)
    private String address;

    @Column(name = "manager_name", nullable = false)
    private String managerName;

    @Column(name = "manager_email", nullable = false)
    private String managerEmail;

    @Column(name = "total_apartments", nullable = false)
    private int totalApartments;

    @Column(name = "emergency_phone", nullable = false)
    private String emergencyPhone;
}