package com.why.buildingmanagement.auth.application.service;

import com.why.buildingmanagement.auth.application.port.in.LoginBuildingUserCommand;
import com.why.buildingmanagement.auth.application.port.in.LoginBuildingUserUseCase;
import com.why.buildingmanagement.auth.application.port.in.RegisterBuildingUserCommand;
import com.why.buildingmanagement.auth.application.port.in.RegisterBuildingUserUseCase;
import com.why.buildingmanagement.auth.application.port.out.LoadBuildingUserPort;
import com.why.buildingmanagement.auth.application.port.out.SaveBuildingUserPort;
import com.why.buildingmanagement.auth.application.port.out.TokenProviderPort;
import com.why.buildingmanagement.auth.domain.exception.DuplicateEmailException;
import com.why.buildingmanagement.auth.domain.exception.DuplicateUsernameException;
import com.why.buildingmanagement.auth.domain.exception.InvalidCredentialsException;
import com.why.buildingmanagement.auth.domain.model.BuildingUser;
import com.why.buildingmanagement.auth.domain.model.BuildingUserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthBuildingUserService implements RegisterBuildingUserUseCase, LoginBuildingUserUseCase {

    private final LoadBuildingUserPort loadBuildingUserPort;
    private final SaveBuildingUserPort saveBuildingUserPort;
    private final TokenProviderPort tokenProviderPort;
    private final PasswordEncoder passwordEncoder;

    public AuthBuildingUserService(LoadBuildingUserPort loadBuildingUserPort,
                                   SaveBuildingUserPort saveBuildingUserPort,
                                   TokenProviderPort tokenProviderPort,
                                   PasswordEncoder passwordEncoder) {
        this.loadBuildingUserPort = loadBuildingUserPort;
        this.saveBuildingUserPort = saveBuildingUserPort;
        this.tokenProviderPort = tokenProviderPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String login(LoginBuildingUserCommand command) {

        BuildingUser buildingUser = loadBuildingUserPort
                .loadByUsernameOrEmail(command.usernameOrEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(command.password(), buildingUser.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return tokenProviderPort.generateToken(buildingUser);
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

}
