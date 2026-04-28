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

    private final BuildingUserRepository buildingUserRepository;

    @Override
    public Optional<BuildingUser> loadByUsernameOrEmail(final String usernameOrEmail) {
        return buildingUserRepository
                .findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(final String username) {
        return buildingUserRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(final String email) {
        return buildingUserRepository.existsByEmail(email);
    }

    @Override
    public Optional<BuildingUser> loadById(final Long id) {
        return buildingUserRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public BuildingUser save(final BuildingUser buildingUser) {
        final BuildingUserEntity entity = toEntity(buildingUser);
        final BuildingUserEntity savedEntity = buildingUserRepository.save(entity);
        return toDomain(savedEntity);
    }

    private BuildingUser toDomain(final BuildingUserEntity entity) {
        return BuildingUser.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .role(BuildingUserRole.valueOf(entity.getRole()))
                .enabled(entity.isEnabled())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private BuildingUserEntity toEntity(final BuildingUser buildingUser) {
        return BuildingUserEntity.builder()
                .id(buildingUser.getId())
                .username(buildingUser.getUsername())
                .email(buildingUser.getEmail())
                .passwordHash(buildingUser.getPasswordHash())
                .role(buildingUser.getRole().name())
                .enabled(buildingUser.isEnabled())
                .createdAt(buildingUser.getCreatedAt())
                .build();
    }
}
