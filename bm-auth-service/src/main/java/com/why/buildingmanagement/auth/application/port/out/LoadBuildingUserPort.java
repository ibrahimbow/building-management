package com.why.buildingmanagement.auth.application.port.out;

import com.why.buildingmanagement.auth.domain.model.BuildingUser;

import java.util.Optional;

public interface LoadBuildingUserPort {
    Optional<BuildingUser> loadByUsernameOrEmail(String usernameOrEmail);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
