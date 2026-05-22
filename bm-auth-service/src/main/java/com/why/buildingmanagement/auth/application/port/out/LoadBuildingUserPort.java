package com.why.buildingmanagement.auth.application.port.out;

import com.why.buildingmanagement.auth.domain.model.BuildingUser;

import java.util.Optional;

public interface LoadBuildingUserPort {

    Optional<BuildingUser> loadByUsernameOrEmail(String usernameOrEmail);

    Optional<BuildingUser> loadById(Long id);

    Optional<BuildingUser> loadByUsername(String username);

    Optional<BuildingUser> loadByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}