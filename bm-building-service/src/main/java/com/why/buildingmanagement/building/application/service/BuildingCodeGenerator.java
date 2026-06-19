package com.why.buildingmanagement.building.application.service;

import com.why.buildingmanagement.building.application.port.out.GenerateBuildingCodePort;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class BuildingCodeGenerator implements GenerateBuildingCodePort {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public String generateCode() {

        final int number = RANDOM.nextInt(900000) + 100000;

        return "BM-" + number;
    }
}
