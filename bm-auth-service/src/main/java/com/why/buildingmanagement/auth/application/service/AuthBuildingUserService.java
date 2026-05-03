package com.why.buildingmanagement.auth.application.service;

import com.why.buildingmanagement.auth.application.port.in.*;
import com.why.buildingmanagement.auth.application.port.out.LoadBuildingUserPort;
import com.why.buildingmanagement.auth.application.port.out.SaveBuildingUserPort;
import com.why.buildingmanagement.auth.application.port.out.TokenProviderPort;
import com.why.buildingmanagement.auth.application.result.LoginResult;
import com.why.buildingmanagement.auth.domain.exception.DuplicateEmailException;
import com.why.buildingmanagement.auth.domain.exception.DuplicateUsernameException;
import com.why.buildingmanagement.auth.domain.exception.InvalidCredentialsException;
import com.why.buildingmanagement.auth.domain.model.BuildingUser;
import com.why.buildingmanagement.auth.domain.model.BuildingUserRole;
import com.why.buildingmanagement.auth.domain.model.RefreshToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthBuildingUserService implements RegisterBuildingUserUseCase,
        LoginBuildingUserUseCase,
        RefreshAccessTokenUseCase,
        LogoutUseCase {

    private final LoadBuildingUserPort loadBuildingUserPort;
    private final SaveBuildingUserPort saveBuildingUserPort;
    private final TokenProviderPort tokenProviderPort;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public AuthBuildingUserService(final LoadBuildingUserPort loadBuildingUserPort,
                                   final SaveBuildingUserPort saveBuildingUserPort,
                                   final TokenProviderPort tokenProviderPort,
                                   final PasswordEncoder passwordEncoder,
                                   final RefreshTokenService refreshTokenService) {
        this.loadBuildingUserPort = loadBuildingUserPort;
        this.saveBuildingUserPort = saveBuildingUserPort;
        this.tokenProviderPort = tokenProviderPort;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public LoginResult login(final LoginBuildingUserCommand command) {

        final BuildingUser buildingUser = loadBuildingUserPort
                .loadByUsernameOrEmail(command.usernameOrEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(command.password(), buildingUser.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        final String accessToken = tokenProviderPort.generateToken(buildingUser);

        final RefreshToken refreshToken =
                refreshTokenService.createForUser(buildingUser.getId());

        return new LoginResult(
                accessToken,
                refreshToken.getToken()
        );
    }

    @Override
    public Long register(final RegisterBuildingUserCommand command) {
        if (loadBuildingUserPort.existsByUsername(command.username())) {
            throw new DuplicateUsernameException(command.username());
        }

        if (loadBuildingUserPort.existsByEmail(command.email())) {
            throw new DuplicateEmailException(command.email());
        }

        final String hash = passwordEncoder.encode(command.password());

        final BuildingUser newBuildingUser = BuildingUser.createNew(
                command.username(),
                command.email(),
                hash,
                command.nickname(),
                BuildingUserRole.TENANT);

        final BuildingUser saved = saveBuildingUserPort.save(newBuildingUser);
        return saved.getId();
    }

    @Override
    public void logout(final LogoutCommand command) {
        final RefreshToken refreshToken = refreshTokenService.validate(command.refreshToken());

        refreshTokenService.deleteForUser(refreshToken.getUserId());
    }


    @Override
    public LoginResult refresh(final RefreshAccessTokenCommand command) {

        final RefreshToken refreshToken =
                refreshTokenService.validate(command.refreshToken());

        final BuildingUser buildingUser = loadBuildingUserPort
                .loadById(refreshToken.getUserId())
                .orElseThrow(InvalidCredentialsException::new);

        final String accessToken =
                tokenProviderPort.generateToken(buildingUser);

        return new LoginResult(
                accessToken,
                refreshToken.getToken()
        );
    }
}
