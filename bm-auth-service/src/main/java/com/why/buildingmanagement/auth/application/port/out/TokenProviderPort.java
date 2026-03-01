package com.why.buildingmanagement.auth.application.port.out;

import com.why.buildingmanagement.auth.domain.model.BuildingUser;

public interface TokenProviderPort {
    String generateToken(BuildingUser buildingUser);
}
