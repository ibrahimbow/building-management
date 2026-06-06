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

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminBuildingUserService implements GetAllBuildingUsersUseCase,
                DisableBuildingUserUseCase {

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
    public void disableUser(final Long userId) {
        currentBuildingUserService.requireAdmin();

        final BuildingUser user = loadBuildingUserPort.loadById(userId)
                        .orElseThrow(() -> new BuildingUserNotFoundException(userId));

        final BuildingUser disabledUser = new BuildingUser(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getPasswordHash(),
                        user.getDisplayName(),
                        user.getPhoneNumber(),
                        user.getAvatarUrl(),
                        user.getRole(),
                        user.getCreatedAt(),
                        false);

        saveBuildingUserPort.save(disabledUser);
    }

    private AdminBuildingUserResult toResult(final BuildingUser user) {
        return new AdminBuildingUserResult(
                        user.getId(),
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