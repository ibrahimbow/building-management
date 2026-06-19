package com.why.buildingmanagement.building.domain.model;

import com.why.buildingmanagement.building.domain.exception.InvalidBuildingException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class Building {

    private static final int MIN_TOTAL_APARTMENTS = 4;
    private static final int MAX_TOTAL_APARTMENTS = 300;

    private final UUID id;
    private final String buildingName;
    private final String code;
    private final String address;
    private final Long managerId;
    private final int totalApartments;
    private final String emergencyPhone;

    private Building(final UUID id,
                     final String buildingName,
                     final String code,
                     final String address,
                     final Long managerId,
                     final int totalApartments,
                     final String emergencyPhone) {
        validate(buildingName, code, address, managerId, totalApartments, emergencyPhone);

        this.id = id;
        this.buildingName = buildingName.trim();
        this.code = code.trim();
        this.address = address.trim();
        this.managerId = managerId;
        this.totalApartments = totalApartments;
        this.emergencyPhone = emergencyPhone.trim();
    }

    public static Building createNew(final String buildingName,
                                     final String code,
                                     final String address,
                                     final Long managerId,
                                     final int totalApartments,
                                     final String emergencyPhone) {
        return new Building(null,
                            buildingName,
                            code,
                            address,
                            managerId,
                            totalApartments,
                            emergencyPhone);
    }

    public static Building restore(final UUID id,
                                   final String buildingName,
                                   final String code,
                                   final String address,
                                   final Long managerId,
                                   final int totalApartments,
                                   final String emergencyPhone) {
        if (id == null) {
            throw new InvalidBuildingException("Building id is required when restoring building");
        }

        return new Building(id,
                            buildingName,
                            code,
                            address,
                            managerId,
                            totalApartments,
                            emergencyPhone);
    }

    public Building updateDetails(final String buildingName,
                                  final String address,
                                  final int totalApartments,
                                  final String emergencyPhone) {
        return new Building(this.id,
                            buildingName,
                            this.code,
                            address,
                            this.managerId,
                            totalApartments,
                            emergencyPhone);
    }

    public boolean isManagedBy(final Long managerId) {
        return this.managerId.equals(managerId);
    }

    private static void validate(final String buildingName,
                                 final String code,
                                 final String address,
                                 final Long managerId,
                                 final int totalApartments,
                                 final String emergencyPhone) {
        requireText(buildingName, "Building name is required");
        requireText(code, "Building code is required");
        requireText(address, "Address is required");
        requireText(emergencyPhone, "Emergency phone is required");

        if (managerId == null) {
            throw new InvalidBuildingException("Manager id is required");
        }

        if (totalApartments < MIN_TOTAL_APARTMENTS) {
            throw new InvalidBuildingException("Total apartments must be at least " + MIN_TOTAL_APARTMENTS);
        }

        if (totalApartments > MAX_TOTAL_APARTMENTS) {
            throw new InvalidBuildingException("Total apartments must be at most " + MAX_TOTAL_APARTMENTS);
        }
    }

    private static void requireText(final String value, final String message) {
        if (value == null || value.isBlank()) {
            throw new InvalidBuildingException(message);
        }
    }
}