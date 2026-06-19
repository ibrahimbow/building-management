package com.why.buildingmanagement.auth.application.service;

import com.why.buildingmanagement.auth.application.port.in.DisableBuildingUserUseCase;
import com.why.buildingmanagement.auth.application.port.in.GetAllBuildingUsersUseCase;
import com.why.buildingmanagement.auth.application.port.out.LoadBuildingUserPort;
import com.why.buildingmanagement.auth.application.port.out.SaveBuildingUserPort;
import com.why.buildingmanagement.auth.application.result.AdminBuildingUserResult;
import com.why.buildingmanagement.auth.domain.exception.BuildingUserNotFoundException;
import com.why.buildingmanagement.auth.domain.model.BuildingUser;
import com.why.buildingmanagement.auth.infrastructure.security.CurrentBuildingUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminBuildingUserService implements GetAllBuildingUsersUseCase, DisableBuildingUserUseCase {

    private final LoadBuildingUserPort loadBuildingUserPort;
    private final SaveBuildingUserPort saveBuildingUserPort;
    private final CurrentBuildingUserService currentBuildingUserService;

    @Override
    public List<AdminBuildingUserResult> getAllUsers() {
        currentBuildingUserService.requireAdmin();

        return loadBuildingUserPort.loadAll()
                                   .stream()
                                   .map(this::toResult)
                                   .toList();
    }

    @Override
    @Transactional
    public void disableUser(final Long userId) {
        Objects.requireNonNull(userId, "userId must not be null");

        currentBuildingUserService.requireAdmin();

        final BuildingUser user = loadUserOrThrow(userId);

        saveBuildingUserPort.save(user.disable());
    }

    private BuildingUser loadUserOrThrow(final Long userId) {
        return loadBuildingUserPort.loadById(userId)
                                   .orElseThrow(() -> new BuildingUserNotFoundException(userId));
    }

    private AdminBuildingUserResult toResult(final BuildingUser user) {
        return new AdminBuildingUserResult(user.getId(),
                                           user.getUsername(),
                                           user.getEmail(),
                                           user.getDisplayName(),
                                           user.getPhoneNumber(),
                                           user.getAvatarUrl(),
                                           user.getRole(),
                                           user.getCreatedAt(),
                                           user.isEnabled());
    }
}