package com.why.buildingmanagement.auth.application.service;

import com.why.buildingmanagement.auth.application.port.in.LoginBuildingUserCommand;
import com.why.buildingmanagement.auth.application.port.in.LoginBuildingUserUseCase;
import com.why.buildingmanagement.auth.application.port.in.RegisterBuildingUserCommand;
import com.why.buildingmanagement.auth.application.port.in.RegisterBuildingUserUseCase;
import com.why.buildingmanagement.auth.application.port.out.LoadBuildingUserPort;
import com.why.buildingmanagement.auth.application.port.out.SaveBuildingUserPort;
import com.why.buildingmanagement.auth.application.port.out.TokenProviderPort;
import com.why.buildingmanagement.auth.domain.model.BuildingUser;
import com.why.buildingmanagement.auth.domain.model.BuildingUserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

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

        if (command.usernameOrEmail() == null || command.password() == null) {
            throw new IllegalArgumentException("Username/email and password must be provided");
        }

        BuildingUser buildingUser = loadBuildingUserPort.loadByUsernameOrEmail(command.usernameOrEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(command.password(), buildingUser.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return tokenProviderPort.generateToken(buildingUser);
    }

    @Override
    public Long register(RegisterBuildingUserCommand command) {
        if (loadBuildingUserPort.existsByUsername(command.username())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (loadBuildingUserPort.existsByEmail(command.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        BuildingUserRole role = command.role() == null ? BuildingUserRole.TENANT : BuildingUserRole.valueOf(command.role());
        String hash = passwordEncoder.encode(command.password());

        BuildingUser newBuildingUser = new BuildingUser(
                null,
                command.username(),
                command.email(),
                hash,
                role,
                Instant.now(),
                true
        );

        BuildingUser saved = saveBuildingUserPort.save(newBuildingUser);
        return saved.getId();
    }

}
