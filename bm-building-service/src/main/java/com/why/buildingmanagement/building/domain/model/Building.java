package com.why.buildingmanagement.building.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.security.SecureRandom;
import java.util.UUID;

@Getter
@Builder
public class Building {

    private static final SecureRandom RANDOM = new SecureRandom();

    private UUID id;
    private String buildingName;
    private String code;
    private String address;
    private String managerName;
    private String managerEmail;
    private int totalApartments;
    private String emergencyPhone;

    public Building(
            UUID id,
            String buildingName,
            String code,
            String address,
            String managerName,
            String managerEmail,
            int totalApartments,
            String emergencyPhone
    ) {
        this.id = id;
        this.buildingName = buildingName;
        this.code = code;
        this.address = address;
        this.managerName = managerName;
        this.managerEmail = managerEmail;
        this.totalApartments = totalApartments;
        this.emergencyPhone = emergencyPhone;
    }

    public static Building createNew(
            String buildingName,
            String code,
            String address,
            String managerName,
            String managerEmail,
            int totalApartments,
            String emergencyPhone
    ) {
        return new Building(
                null,
                buildingName,
                code,
                address,
                managerName,
                managerEmail,
                totalApartments,
                emergencyPhone
        );
    }

    public static String generateCode() {
        int number = RANDOM.nextInt(900000) + 100000;
        return "BM-" + number;
    }
}