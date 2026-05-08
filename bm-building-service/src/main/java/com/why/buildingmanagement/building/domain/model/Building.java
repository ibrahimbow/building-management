package com.why.buildingmanagement.building.domain.model;

import lombok.Getter;

import java.security.SecureRandom;
import java.util.UUID;

@Getter
public class Building {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int MIN_TOTAL_APARTMENTS = 4;

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
        return new Building(
                null,
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
            throw new IllegalArgumentException("Building id is required when restoring building");
        }

        return new Building(
                id,
                buildingName,
                code,
                address,
                managerId,
                totalApartments,
                emergencyPhone
        );
    }

    public Building updateDetails(final String buildingName,
                                  final String address,
                                  final int totalApartments,
                                  final String emergencyPhone) {
        return new Building(
                this.id,
                buildingName,
                this.code,
                address,
                this.managerId,
                totalApartments,
                emergencyPhone
        );
    }

    public Building changeEmergencyPhone(final String emergencyPhone) {
        return new Building(
                this.id,
                this.buildingName,
                this.code,
                this.address,
                this.managerId,
                this.totalApartments,
                emergencyPhone
        );
    }

    public Building rename(final String buildingName) {
        return new Building(
                this.id,
                buildingName,
                this.code,
                this.address,
                this.managerId,
                this.totalApartments,
                this.emergencyPhone
        );
    }

    public static String generateCode() {
        final int number = RANDOM.nextInt(900000) + 100000;
        return "BM-" + number;
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
            throw new IllegalArgumentException("Manager id is required");
        }

        if (totalApartments < MIN_TOTAL_APARTMENTS) {
            throw new IllegalArgumentException(
                    "Total apartments must be at least " + MIN_TOTAL_APARTMENTS
            );
        }
    }

    private static void requireText(final String value, final String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}