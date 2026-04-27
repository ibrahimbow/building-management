package com.why.buildingmanagement.auth.application.service;

import com.why.buildingmanagement.auth.application.port.in.LoginBuildingUserCommand;
import com.why.buildingmanagement.auth.application.port.in.RegisterBuildingUserCommand;
import com.why.buildingmanagement.auth.application.port.out.LoadBuildingUserPort;
import com.why.buildingmanagement.auth.application.port.out.SaveBuildingUserPort;
import com.why.buildingmanagement.auth.application.port.out.TokenProviderPort;
import com.why.buildingmanagement.auth.domain.exception.DuplicateEmailException;
import com.why.buildingmanagement.auth.domain.exception.DuplicateUsernameException;
import com.why.buildingmanagement.auth.domain.exception.InvalidCredentialsException;
import com.why.buildingmanagement.auth.domain.model.BuildingUser;
import com.why.buildingmanagement.auth.domain.model.BuildingUserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthBuildingUserServiceTest {

    @Mock
    private LoadBuildingUserPort loadBuildingUserPort;

    @Mock
    private SaveBuildingUserPort saveBuildingUserPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenProviderPort tokenProviderPort;

    private AuthBuildingUserService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthBuildingUserService(
                loadBuildingUserPort,
                saveBuildingUserPort,
                tokenProviderPort,
                passwordEncoder);
    }

    @Test
    void register_shouldCreateUserSuccessfully() {
        RegisterBuildingUserCommand command = registerCommand();

        when(loadBuildingUserPort.existsByUsername(command.username())).thenReturn(false);
        when(loadBuildingUserPort.existsByEmail(command.email())).thenReturn(false);
        when(passwordEncoder.encode(command.password())).thenReturn("HASHED_PASSWORD");
        when(saveBuildingUserPort.save(any(BuildingUser.class))).thenReturn(savedUser());

        Long result = authService.register(command);

        assertNotNull(result);
        assertEquals(1L, result);

        verify(saveBuildingUserPort).save(argThat(user ->
                user.getUsername().equals("ibrahim")
                        && user.getEmail().equals("ibrahim@test.com")
                        && user.getPasswordHash().equals("HASHED_PASSWORD")
                        && user.getRole() == BuildingUserRole.TENANT
                        && user.isEnabled()));
    }

    @Test
    void register_shouldThrowDuplicateUsernameException_whenUsernameAlreadyExists() {
        RegisterBuildingUserCommand command = registerCommand();

        when(loadBuildingUserPort.existsByUsername(command.username())).thenReturn(true);

        assertThrows(
                DuplicateUsernameException.class,
                () -> authService.register(command)
        );

        verify(loadBuildingUserPort).existsByUsername(command.username());
        verify(loadBuildingUserPort, never()).existsByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(saveBuildingUserPort, never()).save(any());
    }

    @Test
    void register_shouldThrowDuplicateEmailException_whenEmailAlreadyExists() {
        RegisterBuildingUserCommand command = registerCommand();

        when(loadBuildingUserPort.existsByUsername(command.username())).thenReturn(false);
        when(loadBuildingUserPort.existsByEmail(command.email())).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> authService.register(command));

        verify(loadBuildingUserPort).existsByUsername(command.username());
        verify(loadBuildingUserPort).existsByEmail(command.email());
        verify(passwordEncoder, never()).encode(anyString());
        verify(saveBuildingUserPort, never()).save(any());
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        LoginBuildingUserCommand command = loginCommand();
        BuildingUser user = savedUser();

        when(loadBuildingUserPort.loadByUsernameOrEmail(command.usernameOrEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(command.password(), user.getPasswordHash()))
                .thenReturn(true);

        when(tokenProviderPort.generateToken(user))
                .thenReturn("JWT_TOKEN");

        String token = authService.login(command);

        assertEquals("JWT_TOKEN", token);

        verify(loadBuildingUserPort).loadByUsernameOrEmail(command.usernameOrEmail());
        verify(passwordEncoder).matches(command.password(), user.getPasswordHash());
        verify(tokenProviderPort).generateToken(user);
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenUserNotFound() {
        LoginBuildingUserCommand command = loginCommand();

        when(loadBuildingUserPort.loadByUsernameOrEmail(command.usernameOrEmail()))
                .thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(command));

        verify(loadBuildingUserPort).loadByUsernameOrEmail(command.usernameOrEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(tokenProviderPort, never()).generateToken(any());
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenPasswordIsWrong() {
        LoginBuildingUserCommand command = loginCommand();
        BuildingUser user = savedUser();

        when(loadBuildingUserPort.loadByUsernameOrEmail(command.usernameOrEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(command.password(), user.getPasswordHash()))
                .thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(command));

        verify(loadBuildingUserPort).loadByUsernameOrEmail(command.usernameOrEmail());
        verify(passwordEncoder).matches(command.password(), user.getPasswordHash());
        verify(tokenProviderPort, never()).generateToken(any());
    }

    private RegisterBuildingUserCommand registerCommand() {
        return new RegisterBuildingUserCommand(
                "ibrahim",
                "ibrahim@test.com",
                "12345678");
    }

    private LoginBuildingUserCommand loginCommand() {
        return new LoginBuildingUserCommand(
                "ibrahim",
                "12345678"
        );
    }

    private BuildingUser savedUser() {
        return new BuildingUser(
                1L,
                "ibrahim",
                "ibrahim@test.com",
                "HASHED_PASSWORD",
                BuildingUserRole.TENANT,
                Instant.now(),
                true
        );
    }

}