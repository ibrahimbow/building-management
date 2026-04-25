package com.why.buildingmanagement.auth.infrastructure.persistence;


import com.why.buildingmanagement.auth.application.port.out.LoadBuildingUserPort;
import com.why.buildingmanagement.auth.application.port.out.SaveBuildingUserPort;
import com.why.buildingmanagement.auth.domain.model.BuildingUser;
import com.why.buildingmanagement.auth.domain.model.BuildingUserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BuildingUserPersistenceAdapter implements LoadBuildingUserPort, SaveBuildingUserPort {

    private final SpringDataBuildingUserRepository userRepository;

    @Override
    public Optional<BuildingUser> loadByUsernameOrEmail(String usernameOrEmail) {
        return userRepository
                .findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public BuildingUser save(BuildingUser buildingUser) {
        BuildingUserEntity entity = toEntity(buildingUser);
        BuildingUserEntity savedEntity = userRepository.save(entity);
        return toDomain(savedEntity);
    }

    private BuildingUser toDomain(BuildingUserEntity entity) {
        return BuildingUser.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .role(BuildingUserRole.valueOf(entity.getRole()))
                .enabled(entity.getEnabled())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private BuildingUserEntity toEntity(BuildingUser buildingUser) {
        return BuildingUserEntity.builder()
                .id(buildingUser.getId())
                .username(buildingUser.getUsername())
                .email(buildingUser.getEmail())
                .passwordHash(buildingUser.getPasswordHash())
                .role(String.valueOf(buildingUser.getRole()))
                .enabled(buildingUser.isEnabled())
                .createdAt(buildingUser.getCreatedAt())
                .build();
    }
}
