package com.why.buildingmanagement.building.domain.model;

import lombok.Getter;

import java.security.SecureRandom;
import java.util.UUID;

@Getter
public class Building {

    private static final SecureRandom RANDOM = new SecureRandom();

    private UUID id;
    private String buildingName;
    private String code;
    private String address;
    private Long managerId;
    private int totalApartments;
    private String emergencyPhone;

    protected Building() {
        // For frameworks only
    }

    private Building(
            final UUID id,
            final String buildingName,
            final String code,
            final String address,
            final Long managerId,
            final int totalApartments,
            final String emergencyPhone
    ) {
        this.id = id;
        this.buildingName = buildingName;
        this.code = code;
        this.address = address;
        this.managerId = managerId;
        this.totalApartments = totalApartments;
        this.emergencyPhone = emergencyPhone;
    }

    public static Building createNew(
            final String buildingName,
            final String code,
            final String address,
            final Long managerId,
            final int totalApartments,
            final String emergencyPhone
    ) {

        validate(
                buildingName,
                code,
                address,
                managerId,
                totalApartments,
                emergencyPhone
        );

        return new Building(
                null,
                buildingName,
                code,
                address,
                managerId,
                totalApartments,
                emergencyPhone
        );
    }

    public static Building restore(
            final UUID id,
            final String buildingName,
            final String code,
            final String address,
            final Long managerId,
            final int totalApartments,
            final String emergencyPhone
    ) {

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

    public void changeEmergencyPhone(final String phone) {

        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Invalid emergency phone");
        }

        this.emergencyPhone = phone;
    }

    public void assignManager(final Long managerId) {

        if (managerId == null) {
            throw new IllegalArgumentException("Manager id required");
        }

        this.managerId = managerId;
    }

    public void rename(final String newName) {

        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Invalid building name");
        }

        this.buildingName = newName;
    }

    public static String generateCode() {

        final int number = RANDOM.nextInt(900000) + 100000;

        return "BM-" + number;
    }

    private static void validate(
            final String buildingName,
            final String code,
            final String address,
            final Long managerId,
            final int totalApartments,
            final String emergencyPhone) {

        if (buildingName == null || buildingName.isBlank()) {
            throw new IllegalArgumentException("Building name required");
        }

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Building code required");
        }

        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address required");
        }

        if (managerId == null) {
            throw new IllegalArgumentException("Manager id required");
        }

        if (totalApartments < 4) {
            throw new IllegalArgumentException("Total apartments must be at least 4");
        }

        if (emergencyPhone == null || emergencyPhone.isBlank()) {
            throw new IllegalArgumentException("Emergency phone required");
        }
    }
}