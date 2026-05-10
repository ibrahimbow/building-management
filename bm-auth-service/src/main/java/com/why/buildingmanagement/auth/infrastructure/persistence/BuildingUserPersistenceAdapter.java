package com.why.buildingmanagement.auth.infrastructure.persistence;

import com.why.buildingmanagement.auth.application.port.out.LoadBuildingUserPort;
import com.why.buildingmanagement.auth.application.port.out.SaveBuildingUserPort;
import com.why.buildingmanagement.auth.domain.model.BuildingUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BuildingUserPersistenceAdapter implements LoadBuildingUserPort, SaveBuildingUserPort {

    private final BuildingUserRepository buildingUserRepository;
    private final BuildingUserMapper buildingUserMapper;

    @Override
    public Optional<BuildingUser> loadByUsernameOrEmail(final String usernameOrEmail) {

        return buildingUserRepository
                .findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .map(buildingUserMapper::toDomain);
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
                .map(buildingUserMapper::toDomain);
    }

    @Override
    public BuildingUser save(final BuildingUser buildingUser) {

        final BuildingUserEntity savedEntity = buildingUserRepository.save(
                buildingUserMapper.toEntity(buildingUser));

        return buildingUserMapper.toDomain(savedEntity);
    }
}