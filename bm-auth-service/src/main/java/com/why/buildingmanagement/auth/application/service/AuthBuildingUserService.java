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
        LogoutUseCase{

    private final LoadBuildingUserPort loadBuildingUserPort;
    private final SaveBuildingUserPort saveBuildingUserPort;
    private final TokenProviderPort tokenProviderPort;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public AuthBuildingUserService(LoadBuildingUserPort loadBuildingUserPort,
                                   SaveBuildingUserPort saveBuildingUserPort,
                                   TokenProviderPort tokenProviderPort,
                                   PasswordEncoder passwordEncoder,
                                   RefreshTokenService refreshTokenService) {
        this.loadBuildingUserPort = loadBuildingUserPort;
        this.saveBuildingUserPort = saveBuildingUserPort;
        this.tokenProviderPort = tokenProviderPort;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public LoginResult login(LoginBuildingUserCommand command) {

        BuildingUser buildingUser = loadBuildingUserPort
                .loadByUsernameOrEmail(command.usernameOrEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(command.password(), buildingUser.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = tokenProviderPort.generateToken(buildingUser);

        RefreshToken refreshToken =
                refreshTokenService.createForUser(buildingUser.getId());

        return new LoginResult(
                accessToken,
                refreshToken.getToken()
        );
    }

    @Override
    public Long register(RegisterBuildingUserCommand command) {
        if (loadBuildingUserPort.existsByUsername(command.username())) {
            throw new DuplicateUsernameException(command.username());
        }

        if (loadBuildingUserPort.existsByEmail(command.email())) {
            throw new DuplicateEmailException(command.email());
        }

        String hash = passwordEncoder.encode(command.password());

        BuildingUser newBuildingUser = BuildingUser.createNew(
                command.username(),
                command.email(),
                hash,
                BuildingUserRole.TENANT);

        BuildingUser saved = saveBuildingUserPort.save(newBuildingUser);
        return saved.getId();
    }

    @Override
    public void logout(LogoutCommand command) {
        RefreshToken refreshToken = refreshTokenService.validate(command.refreshToken());

        refreshTokenService.deleteForUser(refreshToken.getUserId());
    }


    @Override
    public LoginResult refresh(RefreshAccessTokenCommand command) {

        RefreshToken refreshToken =
                refreshTokenService.validate(command.refreshToken());

        BuildingUser buildingUser = loadBuildingUserPort
                .loadById(refreshToken.getUserId())
                .orElseThrow(InvalidCredentialsException::new);

        String accessToken =
                tokenProviderPort.generateToken(buildingUser);

        return new LoginResult(
                accessToken,
                refreshToken.getToken()
        );
    }
}
