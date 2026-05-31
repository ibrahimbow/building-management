package com.why.buildingmanagement.auth.application.service;

import com.why.buildingmanagement.auth.application.port.in.ChangePasswordCommand;
import com.why.buildingmanagement.auth.application.port.in.LoginBuildingUserCommand;
import com.why.buildingmanagement.auth.application.port.in.RegisterBuildingUserCommand;
import com.why.buildingmanagement.auth.application.port.in.UpdateBuildingUserProfileCommand;
import com.why.buildingmanagement.auth.application.port.out.LoadBuildingUserPort;
import com.why.buildingmanagement.auth.application.port.out.SaveBuildingUserPort;
import com.why.buildingmanagement.auth.application.port.out.TokenProviderPort;
import com.why.buildingmanagement.auth.application.result.BuildingUserProfileResult;
import com.why.buildingmanagement.auth.application.result.LoginResult;
import com.why.buildingmanagement.auth.domain.exception.BuildingUserNotFoundException;
import com.why.buildingmanagement.auth.domain.exception.DuplicateEmailException;
import com.why.buildingmanagement.auth.domain.exception.InvalidCredentialsException;
import com.why.buildingmanagement.auth.domain.model.BuildingUser;
import com.why.buildingmanagement.auth.domain.model.BuildingUserRole;
import com.why.buildingmanagement.auth.domain.model.RefreshToken;
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

    @Mock
    private RefreshTokenService refreshTokenService;

    private AuthBuildingUserService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthBuildingUserService(
                        loadBuildingUserPort,
                        saveBuildingUserPort,
                        tokenProviderPort,
                        passwordEncoder,
                        refreshTokenService);
    }

    @Test
    void register_shouldThrowDuplicateEmailException_whenEmailAlreadyExists() {

        final RegisterBuildingUserCommand command =
                        registerCommand();

        when(loadBuildingUserPort.existsByUsername(command.username()))
                        .thenReturn(false);

        when(loadBuildingUserPort.existsByEmail(command.email()))
                        .thenReturn(true);

        assertThrows(
                        DuplicateEmailException.class,
                        () -> authService.register(command));

        verify(loadBuildingUserPort)
                        .existsByUsername(command.username());

        verify(loadBuildingUserPort)
                        .existsByEmail(command.email());

        verify(passwordEncoder, never())
                        .encode(anyString());

        verify(saveBuildingUserPort, never())
                        .save(any());
    }

    @Test
    void login_shouldReturnAccessAndRefreshToken_whenCredentialsAreValid() {

        final LoginBuildingUserCommand command =
                        loginCommand();

        final BuildingUser user =
                        savedUser();

        when(loadBuildingUserPort.loadByUsernameOrEmail(command.usernameOrEmail()))
                        .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(command.password(), user.getPasswordHash()))
                        .thenReturn(true);

        when(tokenProviderPort.generateToken(user))
                        .thenReturn("JWT_TOKEN");

        final RefreshToken refreshToken = RefreshToken.builder()
                        .userId(user.getId())
                        .token("REFRESH_TOKEN")
                        .build();

        when(refreshTokenService.createForUser(user.getId()))
                        .thenReturn(refreshToken);

        final LoginResult result =
                        authService.login(command);

        assertEquals("JWT_TOKEN", result.accessToken());
        assertEquals("REFRESH_TOKEN", result.refreshToken());

        verify(refreshTokenService)
                        .createForUser(user.getId());

        verify(loadBuildingUserPort)
                        .loadByUsernameOrEmail(command.usernameOrEmail());

        verify(passwordEncoder)
                        .matches(command.password(), user.getPasswordHash());

        verify(tokenProviderPort)
                        .generateToken(user);
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenUserNotFound() {

        final LoginBuildingUserCommand command =
                        loginCommand();

        when(loadBuildingUserPort.loadByUsernameOrEmail(command.usernameOrEmail()))
                        .thenReturn(Optional.empty());

        assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.login(command));

        verify(loadBuildingUserPort)
                        .loadByUsernameOrEmail(command.usernameOrEmail());

        verify(passwordEncoder, never())
                        .matches(anyString(), anyString());

        verify(tokenProviderPort, never())
                        .generateToken(any());
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenPasswordIsWrong() {

        final LoginBuildingUserCommand command =
                        loginCommand();

        final BuildingUser user =
                        savedUser();

        when(loadBuildingUserPort.loadByUsernameOrEmail(command.usernameOrEmail()))
                        .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(command.password(), user.getPasswordHash()))
                        .thenReturn(false);

        assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.login(command));

        verify(loadBuildingUserPort)
                        .loadByUsernameOrEmail(command.usernameOrEmail());

        verify(passwordEncoder)
                        .matches(command.password(), user.getPasswordHash());

        verify(tokenProviderPort, never())
                        .generateToken(any());
    }

    @Test
    void updateProfile_shouldUpdateProfileAndReturnResult_whenUserExists() {

        final BuildingUser existingUser =
                        savedUser();

        final UpdateBuildingUserProfileCommand command =
                        new UpdateBuildingUserProfileCommand(
                                        existingUser.getId(),
                                        "Ibrahim Alolofi",
                                        "+32465570653",
                                        "/api/files/PROFILE_AVATAR/avatar.png",
                                        "EN",
                                        true);

        final BuildingUser savedUser = new BuildingUser(
                        existingUser.getId(),
                        existingUser.getUsername(),
                        existingUser.getEmail(),
                        existingUser.getPasswordHash(),
                        command.displayName(),
                        command.phoneNumber(),
                        command.avatarUrl(),
                        existingUser.getRole(),
                        existingUser.getCreatedAt(),
                        existingUser.isEnabled());

        when(loadBuildingUserPort.loadById(command.userId()))
                        .thenReturn(Optional.of(existingUser));

        when(saveBuildingUserPort.save(any(BuildingUser.class)))
                        .thenReturn(savedUser);

        final BuildingUserProfileResult result =
                        authService.updateProfile(command);

        assertEquals(existingUser.getId(), result.id());
        assertEquals(existingUser.getUsername(), result.username());
        assertEquals(existingUser.getEmail(), result.email());
        assertEquals("Ibrahim Alolofi", result.displayName());
        assertEquals("+32465570653", result.phoneNumber());
        assertEquals("/api/files/PROFILE_AVATAR/avatar.png", result.avatarUrl());
        assertEquals("EN", result.preferredLanguage());
        assertTrue(result.notificationsEnabled());
        assertEquals(BuildingUserRole.TENANT.name(), result.role());

        verify(loadBuildingUserPort)
                        .loadById(command.userId());

        verify(saveBuildingUserPort)
                        .save(any(BuildingUser.class));
    }

    @Test
    void updateProfile_shouldPreserveExistingAvatar_whenAvatarUrlIsNull() {

        final BuildingUser existingUser = new BuildingUser(
                        1L,
                        "ibrahim",
                        "ibrahim@test.com",
                        "HASHED_PASSWORD",
                        "ibrahimbow",
                        "+32000000000",
                        "/api/files/PROFILE_AVATAR/existing.png",
                        BuildingUserRole.TENANT,
                        Instant.now(),
                        true);

        final UpdateBuildingUserProfileCommand command =
                        new UpdateBuildingUserProfileCommand(
                                        existingUser.getId(),
                                        "Ibrahim Alolofi",
                                        "+32465570653",
                                        null,
                                        "EN",
                                        true);

        final BuildingUser savedUser = new BuildingUser(
                        existingUser.getId(),
                        existingUser.getUsername(),
                        existingUser.getEmail(),
                        existingUser.getPasswordHash(),
                        command.displayName(),
                        command.phoneNumber(),
                        existingUser.getAvatarUrl(),
                        existingUser.getRole(),
                        existingUser.getCreatedAt(),
                        existingUser.isEnabled());

        when(loadBuildingUserPort.loadById(command.userId()))
                        .thenReturn(Optional.of(existingUser));

        when(saveBuildingUserPort.save(any(BuildingUser.class)))
                        .thenReturn(savedUser);

        final BuildingUserProfileResult result =
                        authService.updateProfile(command);

        assertEquals("/api/files/PROFILE_AVATAR/existing.png", result.avatarUrl());

        verify(loadBuildingUserPort)
                        .loadById(command.userId());

        verify(saveBuildingUserPort)
                        .save(any(BuildingUser.class));
    }

    @Test
    void updateProfile_shouldThrowInvalidCredentialsException_whenUserDoesNotExist() {

        final UpdateBuildingUserProfileCommand command =
                        new UpdateBuildingUserProfileCommand(
                                        999L,
                                        "Ibrahim Alolofi",
                                        "+32465570653",
                                        null,
                                        "EN",
                                        true);

        when(loadBuildingUserPort.loadById(command.userId()))
                        .thenReturn(Optional.empty());

        assertThrows(
                        BuildingUserNotFoundException.class,
                        () -> authService.updateProfile(command));

        verify(loadBuildingUserPort)
                        .loadById(command.userId());

        verify(saveBuildingUserPort, never())
                        .save(any());
    }


    @Test
    void shouldChangePassword() {

        final BuildingUser existingUser = new BuildingUser(
                        1L,
                        "ibrahim",
                        "ibrahim@test.com",
                        "OLD_HASH",
                        "Ibrahim",
                        "+3200000000",
                        null,
                        BuildingUserRole.MANAGER,
                        Instant.now(),
                        true);

        when(loadBuildingUserPort.loadById(1L))
                        .thenReturn(Optional.of(existingUser));

        when(passwordEncoder.matches(
                        "OldPassword123!",
                        "OLD_HASH"))
                        .thenReturn(true);

        when(passwordEncoder.encode("NewPassword123!"))
                        .thenReturn("NEW_HASH");

        authService.changePassword(
                        new ChangePasswordCommand(
                                        1L,
                                        "OldPassword123!",
                                        "NewPassword123!"));

        verify(saveBuildingUserPort).save(
                        argThat(user ->
                                        user.getPasswordHash().equals("NEW_HASH")));
    }

    @Test
    void shouldThrowException_whenCurrentPasswordIsInvalid() {

        final BuildingUser existingUser = new BuildingUser(
                        1L,
                        "ibrahim",
                        "ibrahim@test.com",
                        "OLD_HASH",
                        "Ibrahim",
                        "+3200000000",
                        null,
                        BuildingUserRole.MANAGER,
                        Instant.now(),
                        true);

        when(loadBuildingUserPort.loadById(1L))
                        .thenReturn(Optional.of(existingUser));

        when(passwordEncoder.matches(
                        "WrongPassword",
                        "OLD_HASH"))
                        .thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                        () -> authService.changePassword(
                                        new ChangePasswordCommand(
                                                        1L,
                                                        "WrongPassword",
                                                        "NewPassword123!")));

        verify(saveBuildingUserPort, never()).save(any());
    }

    private RegisterBuildingUserCommand registerCommand() {

        return new RegisterBuildingUserCommand(
                        "ibrahim",
                        "ibrahim@test.com",
                        "12345678",
                        "ibrahimbow",
                        "+32000000000",
                        BuildingUserRole.TENANT.name());
    }

    private LoginBuildingUserCommand loginCommand() {

        return new LoginBuildingUserCommand(
                        "ibrahim",
                        "12345678");
    }

    private BuildingUser savedUser() {

        return new BuildingUser(
                        1L,
                        "ibrahim",
                        "ibrahim@test.com",
                        "HASHED_PASSWORD",
                        "ibrahimbow",
                        "+32000000000",
                        null,
                        BuildingUserRole.TENANT,
                        Instant.now(),
                        true);
    }
}