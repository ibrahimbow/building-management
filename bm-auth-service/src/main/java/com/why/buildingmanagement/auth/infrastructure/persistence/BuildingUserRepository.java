package com.why.buildingmanagement.auth.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuildingUserRepository extends JpaRepository<BuildingUserEntity,Long> {

    Optional<BuildingUserEntity> findByUsername(String username);

    Optional<BuildingUserEntity> findByEmail(String email);

    Optional<BuildingUserEntity> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
