package com.why.buildingmanagement.auth.application.service;

import com.why.buildingmanagement.auth.application.port.in.*;
import com.why.buildingmanagement.auth.application.port.out.LoadBuildingUserPort;
import com.why.buildingmanagement.auth.application.port.out.PasswordEncoderPort;
import com.why.buildingmanagement.auth.application.port.out.SaveBuildingUserPort;
import com.why.buildingmanagement.auth.application.port.out.TokenProviderPort;
import com.why.buildingmanagement.auth.application.result.BuildingUserProfileResult;
import com.why.buildingmanagement.auth.application.result.LoginResult;
import com.why.buildingmanagement.auth.domain.exception.BuildingUserNotFoundException;
import com.why.buildingmanagement.auth.domain.exception.DuplicateEmailException;
import com.why.buildingmanagement.auth.domain.exception.DuplicateUsernameException;
import com.why.buildingmanagement.auth.domain.exception.InvalidCredentialsException;
import com.why.buildingmanagement.auth.domain.model.BuildingUser;
import com.why.buildingmanagement.auth.domain.model.RefreshToken;
import com.why.buildingmanagement.auth.infrastructure.kafka.event.AuditEventType;
import com.why.buildingmanagement.auth.infrastructure.kafka.publisher.AuditEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.Optional;

import static com.why.buildingmanagement.auth.domain.model.BuildingUserRole.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthBuildingUserServiceTest {

    private static final Long USER_ID = 1L;
    private static final String USERNAME = "ibrahim";
    private static final String EMAIL = "ibrahim@test.com";
    private static final String RAW_PASSWORD = "Password123!";
    private static final String HASHED_PASSWORD = "HASHED_PASSWORD";
    private static final String ACCESS_TOKEN = "JWT_TOKEN";
    private static final String REFRESH_TOKEN_VALUE = "REFRESH_TOKEN";

    @Mock
    private LoadBuildingUserPort loadBuildingUserPort;

    @Mock
    private SaveBuildingUserPort saveBuildingUserPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private TokenProviderPort tokenProviderPort;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthBuildingUserService authService;

    @Mock
    private AuditEventPublisher auditEventPublisher;

    @Test
    void register_shouldCreateUserSuccessfully() {
        final RegisterBuildingUserCommand command = registerCommand();

        when(loadBuildingUserPort.existsByUsername(command.username()))
                        .thenReturn(false);

        when(loadBuildingUserPort.existsByEmail(command.email()))
                        .thenReturn(false);

        when(passwordEncoderPort.encode(command.password()))
                        .thenReturn(HASHED_PASSWORD);

        when(saveBuildingUserPort.save(any(BuildingUser.class)))
                        .thenReturn(savedUser());

        final Long result = authService.register(command);

        assertThat(result).isEqualTo(USER_ID);

        verify(loadBuildingUserPort).existsByUsername(command.username());
        verify(loadBuildingUserPort).existsByEmail(command.email());
        verify(passwordEncoderPort).encode(command.password());

        verify(saveBuildingUserPort).save(argThat(user ->
                                                                  user.getUsername().equals(command.username())
                                                                                  && user.getEmail().equals(command.email())
                                                                                  && user.getPasswordHash().equals(HASHED_PASSWORD)
                                                                                  && user.getDisplayName().equals(command.displayName())
                                                                                  && user.getPhoneNumber().equals(command.phoneNumber())
                                                                                  && user.getRole() == TENANT
                                                                                  && user.isEnabled()));

        verifyNoInteractions(tokenProviderPort, refreshTokenService);
    }

    @Test
    void register_shouldThrowDuplicateUsernameException_whenUsernameAlreadyExists() {
        final RegisterBuildingUserCommand command = registerCommand();

        when(loadBuildingUserPort.existsByUsername(command.username()))
                        .thenReturn(true);

        assertThatThrownBy(() -> authService.register(command))
                        .isInstanceOf(DuplicateUsernameException.class);

        verify(loadBuildingUserPort).existsByUsername(command.username());
        verify(loadBuildingUserPort, never()).existsByEmail(anyString());
        verify(passwordEncoderPort, never()).encode(anyString());
        verify(saveBuildingUserPort, never()).save(any());
    }

    @Test
    void register_shouldThrowDuplicateEmailException_whenEmailAlreadyExists() {
        final RegisterBuildingUserCommand command = registerCommand();

        when(loadBuildingUserPort.existsByUsername(command.username()))
                        .thenReturn(false);

        when(loadBuildingUserPort.existsByEmail(command.email()))
                        .thenReturn(true);

        assertThatThrownBy(() -> authService.register(command))
                        .isInstanceOf(DuplicateEmailException.class);

        verify(loadBuildingUserPort).existsByUsername(command.username());
        verify(loadBuildingUserPort).existsByEmail(command.email());
        verify(passwordEncoderPort, never()).encode(anyString());
        verify(saveBuildingUserPort, never()).save(any());
    }

    @Test
    void register_shouldThrowAccessDeniedException_whenRoleIsAdmin() {
        final RegisterBuildingUserCommand command = new RegisterBuildingUserCommand(USERNAME,
                                                                                    EMAIL,
                                                                                    RAW_PASSWORD,
                                                                                    "Ibrahim",
                                                                                    "+32000000000",
                                                                                    ADMIN.name());

        assertThatThrownBy(() -> authService.register(command)).isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(loadBuildingUserPort);
        verifyNoInteractions(passwordEncoderPort);
        verifyNoInteractions(saveBuildingUserPort);
    }

    @Test
    void login_shouldReturnAccessAndRefreshToken_whenCredentialsAreValid() {
        final LoginBuildingUserCommand command = loginCommand();
        final BuildingUser user = savedUser();
        final RefreshToken refreshToken = refreshToken();

        when(loadBuildingUserPort.loadByUsernameOrEmail(command.usernameOrEmail()))
                        .thenReturn(Optional.of(user));

        when(passwordEncoderPort.matches(command.password(), user.getPasswordHash()))
                        .thenReturn(true);

        when(tokenProviderPort.generateToken(user))
                        .thenReturn(ACCESS_TOKEN);

        when(refreshTokenService.createForUser(user.getId()))
                        .thenReturn(refreshToken);

        final LoginResult result = authService.login(command);

        assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(result.refreshToken()).isEqualTo(REFRESH_TOKEN_VALUE);

        verify(loadBuildingUserPort).loadByUsernameOrEmail(command.usernameOrEmail());
        verify(passwordEncoderPort).matches(command.password(), user.getPasswordHash());
        verify(tokenProviderPort).generateToken(user);
        verify(refreshTokenService).createForUser(user.getId());
        verifyNoInteractions(saveBuildingUserPort);
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenUserNotFound() {
        final LoginBuildingUserCommand command = loginCommand();

        when(loadBuildingUserPort.loadByUsernameOrEmail(command.usernameOrEmail()))
                        .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(command)).isInstanceOf(InvalidCredentialsException.class);

        verify(loadBuildingUserPort).loadByUsernameOrEmail(command.usernameOrEmail());
        verifyNoInteractions(passwordEncoderPort, tokenProviderPort, refreshTokenService, saveBuildingUserPort);
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenUserIsDisabled() {
        final LoginBuildingUserCommand command = loginCommand();

        when(loadBuildingUserPort.loadByUsernameOrEmail(command.usernameOrEmail()))
                        .thenReturn(Optional.of(disabledUser()));

        assertThatThrownBy(() -> authService.login(command)).isInstanceOf(InvalidCredentialsException.class);

        verify(loadBuildingUserPort).loadByUsernameOrEmail(command.usernameOrEmail());
        verifyNoInteractions(passwordEncoderPort, tokenProviderPort, refreshTokenService, saveBuildingUserPort);
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenPasswordIsWrong() {
        final LoginBuildingUserCommand command = loginCommand();
        final BuildingUser user = savedUser();

        when(loadBuildingUserPort.loadByUsernameOrEmail(command.usernameOrEmail()))
                        .thenReturn(Optional.of(user));

        when(passwordEncoderPort.matches(command.password(), user.getPasswordHash()))
                        .thenReturn(false);

        assertThatThrownBy(() -> authService.login(command)).isInstanceOf(InvalidCredentialsException.class);

        verify(loadBuildingUserPort).loadByUsernameOrEmail(command.usernameOrEmail());
        verify(passwordEncoderPort).matches(command.password(), user.getPasswordHash());
        verifyNoInteractions(tokenProviderPort, refreshTokenService, saveBuildingUserPort);
    }

    @Test
    void updateProfile_shouldUpdateProfileAndReturnResult_whenUserExists() {
        final BuildingUser existingUser = savedUser();

        final UpdateBuildingUserProfileCommand command = new UpdateBuildingUserProfileCommand(existingUser.getId(),
                                                                                              "Ibrahim Alolofi",
                                                                                              "+32465570653",
                                                                                              "/api/files/PROFILE_AVATAR/avatar.png",
                                                                                              "EN",
                                                                                              true);

        final BuildingUser updatedUser = existingUser.updateProfile(command.displayName(),
                                                                    command.phoneNumber(),
                                                                    command.avatarUrl());

        when(loadBuildingUserPort.loadById(command.userId()))
                        .thenReturn(Optional.of(existingUser));

        when(saveBuildingUserPort.save(any(BuildingUser.class)))
                        .thenReturn(updatedUser);

        final BuildingUserProfileResult result = authService.updateProfile(command);

        assertThat(result.id()).isEqualTo(existingUser.getId());
        assertThat(result.username()).isEqualTo(existingUser.getUsername());
        assertThat(result.email()).isEqualTo(existingUser.getEmail());
        assertThat(result.displayName()).isEqualTo("Ibrahim Alolofi");
        assertThat(result.phoneNumber()).isEqualTo("+32465570653");
        assertThat(result.avatarUrl()).isEqualTo("/api/files/PROFILE_AVATAR/avatar.png");
        assertThat(result.preferredLanguage()).isEqualTo("EN");
        assertThat(result.notificationsEnabled()).isTrue();
        assertThat(result.role()).isEqualTo(TENANT.name());

        verify(loadBuildingUserPort).loadById(command.userId());
        verify(saveBuildingUserPort).save(argThat(user -> user.getDisplayName().equals(command.displayName())
                        && user.getPhoneNumber().equals(command.phoneNumber())
                        && user.getAvatarUrl().equals(command.avatarUrl())));
    }

    @Test
    void updateProfile_shouldPreserveExistingAvatar_whenAvatarUrlIsNull() {
        final BuildingUser existingUser = userWithAvatar();

        final UpdateBuildingUserProfileCommand command = new UpdateBuildingUserProfileCommand(existingUser.getId(),
                                                                                              "Ibrahim Alolofi",
                                                                                              "+32465570653",
                                                                                              null,
                                                                                              "EN",
                                                                                              true);

        final BuildingUser updatedUser = existingUser.updateProfile(
                        command.displayName(),
                        command.phoneNumber(),
                        command.avatarUrl());

        when(loadBuildingUserPort.loadById(command.userId()))
                        .thenReturn(Optional.of(existingUser));

        when(saveBuildingUserPort.save(any(BuildingUser.class)))
                        .thenReturn(updatedUser);

        final BuildingUserProfileResult result = authService.updateProfile(command);

        assertThat(result.avatarUrl()).isEqualTo("/api/files/PROFILE_AVATAR/existing.png");

        verify(loadBuildingUserPort).loadById(command.userId());
        verify(saveBuildingUserPort).save(argThat(user ->
                                                                  user.getAvatarUrl().equals(existingUser.getAvatarUrl())));
    }

    @Test
    void updateProfile_shouldThrowBuildingUserNotFoundException_whenUserDoesNotExist() {
        final UpdateBuildingUserProfileCommand command = new UpdateBuildingUserProfileCommand(999L,
                                                                                              "Ibrahim Alolofi",
                                                                                              "+32465570653",
                                                                                              null,
                                                                                              "EN",
                                                                                              true);

        when(loadBuildingUserPort.loadById(command.userId()))
                        .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.updateProfile(command)).isInstanceOf(BuildingUserNotFoundException.class);

        verify(loadBuildingUserPort).loadById(command.userId());
        verify(saveBuildingUserPort, never()).save(any());
    }

    @Test
    void changePassword_shouldChangePassword_whenCurrentPasswordIsValid() {

        final BuildingUser existingUser = managerUser();

        when(loadBuildingUserPort.loadById(USER_ID))
                        .thenReturn(Optional.of(existingUser));

        when(passwordEncoderPort.matches("OldPassword123!", "OLD_HASH"))
                        .thenReturn(true);

        when(passwordEncoderPort.encode("NewPassword123!"))
                        .thenReturn("NEW_HASH");

        when(saveBuildingUserPort.save(any(BuildingUser.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

        authService.changePassword(new ChangePasswordCommand(USER_ID,
                                                             "OldPassword123!",
                                                             "NewPassword123!"));

        verify(loadBuildingUserPort).loadById(USER_ID);
        verify(passwordEncoderPort).matches("OldPassword123!", "OLD_HASH");
        verify(passwordEncoderPort).encode("NewPassword123!");

        verify(saveBuildingUserPort).save(argThat(user -> user.getPasswordHash().equals("NEW_HASH")
                        && user.getUsername().equals(existingUser.getUsername())
                        && user.getRole() == MANAGER));

        verify(auditEventPublisher).publish(USER_ID,
                                            existingUser.getUsername(),
                                            AuditEventType.PASSWORD_CHANGED,
                                            "User password changed successfully");
    }

    @Test
    void changePassword_shouldThrowInvalidCredentialsException_whenCurrentPasswordIsInvalid() {
        final BuildingUser existingUser = managerUser();

        when(loadBuildingUserPort.loadById(USER_ID))
                        .thenReturn(Optional.of(existingUser));

        when(passwordEncoderPort.matches("WrongPassword", "OLD_HASH"))
                        .thenReturn(false);

        assertThatThrownBy(() -> authService.changePassword(new ChangePasswordCommand(USER_ID,
                                                                                      "WrongPassword",
                                                                                      "NewPassword123!")))
                        .isInstanceOf(InvalidCredentialsException.class);

        verify(loadBuildingUserPort).loadById(USER_ID);
        verify(passwordEncoderPort).matches("WrongPassword", "OLD_HASH");
        verify(passwordEncoderPort, never()).encode(anyString());
        verify(saveBuildingUserPort, never()).save(any());
    }

    @Test
    void changePassword_shouldThrowBuildingUserNotFoundException_whenUserDoesNotExist() {
        when(loadBuildingUserPort.loadById(USER_ID))
                        .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.changePassword(new ChangePasswordCommand(USER_ID,
                                                                                      "OldPassword123!",
                                                                                      "NewPassword123!")))
                        .isInstanceOf(BuildingUserNotFoundException.class);

        verify(loadBuildingUserPort).loadById(USER_ID);
        verifyNoInteractions(passwordEncoderPort, saveBuildingUserPort);
    }

    @Test
    void refresh_shouldReturnNewAccessToken_whenRefreshTokenIsValidAndUserIsActive() {
        final RefreshToken refreshToken = refreshToken();
        final BuildingUser user = savedUser();

        when(refreshTokenService.validate(REFRESH_TOKEN_VALUE))
                        .thenReturn(refreshToken);

        when(loadBuildingUserPort.loadById(USER_ID))
                        .thenReturn(Optional.of(user));

        when(tokenProviderPort.generateToken(user))
                        .thenReturn(ACCESS_TOKEN);

        final LoginResult result = authService.refresh(new RefreshAccessTokenCommand(REFRESH_TOKEN_VALUE));

        assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(result.refreshToken()).isEqualTo(REFRESH_TOKEN_VALUE);

        verify(refreshTokenService).validate(REFRESH_TOKEN_VALUE);
        verify(loadBuildingUserPort).loadById(USER_ID);
        verify(tokenProviderPort).generateToken(user);
        verifyNoInteractions(passwordEncoderPort, saveBuildingUserPort);
    }

    @Test
    void refresh_shouldThrowInvalidCredentialsException_whenUserDoesNotExist() {
        final RefreshToken refreshToken = refreshToken();

        when(refreshTokenService.validate(REFRESH_TOKEN_VALUE))
                        .thenReturn(refreshToken);

        when(loadBuildingUserPort.loadById(USER_ID))
                        .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh(new RefreshAccessTokenCommand(REFRESH_TOKEN_VALUE)))
                        .isInstanceOf(InvalidCredentialsException.class);

        verify(refreshTokenService).validate(REFRESH_TOKEN_VALUE);
        verify(loadBuildingUserPort).loadById(USER_ID);
        verifyNoInteractions(tokenProviderPort, passwordEncoderPort, saveBuildingUserPort);
    }

    @Test
    void refresh_shouldThrowInvalidCredentialsException_whenUserIsDisabled() {
        final RefreshToken refreshToken = refreshToken();

        when(refreshTokenService.validate(REFRESH_TOKEN_VALUE))
                        .thenReturn(refreshToken);

        when(loadBuildingUserPort.loadById(USER_ID))
                        .thenReturn(Optional.of(disabledUser()));

        assertThatThrownBy(() -> authService.refresh(new RefreshAccessTokenCommand(REFRESH_TOKEN_VALUE)))
                        .isInstanceOf(InvalidCredentialsException.class);

        verify(refreshTokenService).validate(REFRESH_TOKEN_VALUE);
        verify(loadBuildingUserPort).loadById(USER_ID);
        verifyNoInteractions(tokenProviderPort, passwordEncoderPort, saveBuildingUserPort);
    }

    @Test
    void logout_shouldValidateRefreshTokenAndDeleteForUser() {
        final RefreshToken refreshToken = refreshToken();

        when(refreshTokenService.validate(REFRESH_TOKEN_VALUE))
                        .thenReturn(refreshToken);

        authService.logout(new LogoutCommand(REFRESH_TOKEN_VALUE));

        verify(refreshTokenService).validate(REFRESH_TOKEN_VALUE);
        verify(refreshTokenService).deleteForUser(USER_ID);
        verifyNoInteractions(loadBuildingUserPort, saveBuildingUserPort, passwordEncoderPort, tokenProviderPort);
    }

    private RegisterBuildingUserCommand registerCommand() {
        return new RegisterBuildingUserCommand(USERNAME,
                                               EMAIL,
                                               RAW_PASSWORD,
                                               "ibrahimbow",
                                               "+32000000000",
                                               TENANT.name());
    }

    private LoginBuildingUserCommand loginCommand() {
        return new LoginBuildingUserCommand(USERNAME, RAW_PASSWORD);
    }

    private BuildingUser savedUser() {
        return BuildingUser.restore(USER_ID,
                                    USERNAME,
                                    EMAIL,
                                    HASHED_PASSWORD,
                                    "ibrahimbow",
                                    "+32000000000",
                                    null,
                                    TENANT,
                                    Instant.now(),
                                    true);
    }

    private BuildingUser disabledUser() {
        return BuildingUser.restore(USER_ID,
                                    USERNAME,
                                    EMAIL,
                                    HASHED_PASSWORD,
                                    "ibrahimbow",
                                    "+32000000000",
                                    null,
                                    TENANT,
                                    Instant.now(),
                                    false);
    }

    private BuildingUser userWithAvatar() {
        return BuildingUser.restore(USER_ID,
                                    USERNAME,
                                    EMAIL,
                                    HASHED_PASSWORD,
                                    "ibrahimbow",
                                    "+32000000000",
                                    "/api/files/PROFILE_AVATAR/existing.png",
                                    TENANT,
                                    Instant.now(),
                                    true);
    }

    private BuildingUser managerUser() {
        return BuildingUser.restore(USER_ID,
                                    USERNAME,
                                    EMAIL,
                                    "OLD_HASH",
                                    "Ibrahim",
                                    "+3200000000",
                                    null,
                                    MANAGER,
                                    Instant.now(),
                                    true);
    }

    private RefreshToken refreshToken() {
        return RefreshToken.restore(1L,
                                    USER_ID,
                                    REFRESH_TOKEN_VALUE,
                                    Instant.now().plusSeconds(3600),
                                    false,
                                    Instant.now());
    }
}