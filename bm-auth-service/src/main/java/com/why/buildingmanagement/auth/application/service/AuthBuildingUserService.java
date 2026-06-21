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
import com.why.buildingmanagement.auth.domain.model.BuildingUserRole;
import com.why.buildingmanagement.auth.domain.model.RefreshToken;
import com.why.buildingmanagement.auth.infrastructure.kafka.event.AuditEventType;
import com.why.buildingmanagement.auth.infrastructure.kafka.publisher.AuditEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthBuildingUserService implements RegisterBuildingUserUseCase,
                                                LoginBuildingUserUseCase,
                                                RefreshAccessTokenUseCase,
                                                LogoutUseCase,
                                                UpdateBuildingUserProfileUseCase,
                                                ChangePasswordUseCase {

    private final LoadBuildingUserPort loadBuildingUserPort;
    private final SaveBuildingUserPort saveBuildingUserPort;
    private final TokenProviderPort tokenProviderPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final RefreshTokenService refreshTokenService;
    private final AuditEventPublisher auditEventPublisher;

    @Override
    @Transactional
    public LoginResult login(final LoginBuildingUserCommand command) {
        Objects.requireNonNull(command, "LoginBuildingUserCommand must not be null");

        try {
            final BuildingUser buildingUser = loadActiveUserByUsernameOrEmail(command.usernameOrEmail());

            validatePassword(command.password(), buildingUser.getPasswordHash());

            final String accessToken = tokenProviderPort.generateToken(buildingUser);
            final RefreshToken refreshToken = refreshTokenService.createForUser(buildingUser.getId());

            publishAuditEventSafely(buildingUser.getId(),
                                    buildingUser.getUsername(),
                                    AuditEventType.USER_LOGIN_SUCCESS,
                                    "User logged in successfully");

            return new LoginResult(accessToken, refreshToken.getToken());

        } catch (final InvalidCredentialsException exception) {
            publishAuditEventSafely(null,
                                    command.usernameOrEmail(),
                                    AuditEventType.USER_LOGIN_FAILED,
                                    "Failed login attempt");

            throw exception;
        }
    }

    @Override
    @Transactional
    public Long register(final RegisterBuildingUserCommand command) {
        Objects.requireNonNull(command, "RegisterBuildingUserCommand must not be null");

        final BuildingUserRole role = resolveRegistrationRole(command.role());

        validateUsernameIsUnique(command.username());
        validateEmailIsUnique(command.email());

        final String passwordHash = passwordEncoderPort.encode(command.password());

        final BuildingUser newBuildingUser = BuildingUser.createNew(
                        command.username(),
                        command.email(),
                        passwordHash,
                        command.displayName(),
                        command.phoneNumber(),
                        role);

        final BuildingUser savedUser = saveBuildingUserPort.save(newBuildingUser);

        publishAuditEventSafely(savedUser.getId(),
                                savedUser.getUsername(),
                                AuditEventType.USER_REGISTERED,
                                "User registered successfully");

        return savedUser.getId();
    }

    @Override
    @Transactional
    public BuildingUserProfileResult updateProfile(final UpdateBuildingUserProfileCommand command) {
        Objects.requireNonNull(command, "UpdateBuildingUserProfileCommand must not be null");

        final BuildingUser existingUser = loadUserOrThrow(command.userId());

        final BuildingUser updatedUser = existingUser.updateProfile(
                        command.displayName(),
                        command.phoneNumber(),
                        command.avatarUrl());

        final BuildingUser savedUser = saveBuildingUserPort.save(updatedUser);

        publishAuditEventSafely(savedUser.getId(),
                                savedUser.getUsername(),
                                AuditEventType.PROFILE_UPDATED,
                                "User profile updated successfully");

        return toProfileResult(savedUser,
                               command.preferredLanguage(),
                               command.notificationsEnabled());
    }

    @Override
    @Transactional
    public void logout(final LogoutCommand command) {
        Objects.requireNonNull(command, "LogoutCommand must not be null");

        final RefreshToken refreshToken = refreshTokenService.validate(command.refreshToken());

        refreshTokenService.deleteForUser(refreshToken.getUserId());
    }

    @Override
    public LoginResult refresh(final RefreshAccessTokenCommand command) {
        Objects.requireNonNull(command, "RefreshAccessTokenCommand must not be null");

        final RefreshToken refreshToken = refreshTokenService.validate(command.refreshToken());

        final BuildingUser buildingUser = loadActiveUserById(refreshToken.getUserId());

        final String accessToken = tokenProviderPort.generateToken(buildingUser);

        return new LoginResult(accessToken, refreshToken.getToken());
    }

    @Override
    @Transactional
    public void changePassword(final ChangePasswordCommand command) {
        Objects.requireNonNull(command, "ChangePasswordCommand must not be null");

        final BuildingUser existingUser = loadUserOrThrow(command.userId());

        validatePassword(command.currentPassword(), existingUser.getPasswordHash());

        final String newPasswordHash = passwordEncoderPort.encode(command.newPassword());

        final BuildingUser savedUser = saveBuildingUserPort.save(existingUser.changePassword(newPasswordHash));

        publishAuditEventSafely(savedUser.getId(),
                                savedUser.getUsername(),
                                AuditEventType.PASSWORD_CHANGED,
                                "User password changed successfully");
    }

    private void publishAuditEventSafely(final Long userId,
                                         final String username,
                                         final AuditEventType eventType,
                                         final String description) {
        try {
            auditEventPublisher.publish(userId, username, eventType, description);
        } catch (final Exception exception) {
            log.warn("Failed to publish audit event. Type: {}, UserId: {}",
                     eventType,
                     userId,
                     exception);
        }
    }

    private BuildingUser loadActiveUserByUsernameOrEmail(final String usernameOrEmail) {
        final BuildingUser buildingUser = loadBuildingUserPort.loadByUsernameOrEmail(usernameOrEmail)
                                                              .orElseThrow(InvalidCredentialsException::new);

        ensureUserIsEnabled(buildingUser);

        return buildingUser;
    }

    private BuildingUser loadActiveUserById(final Long userId) {
        final BuildingUser buildingUser = loadBuildingUserPort.loadById(userId)
                                                              .orElseThrow(InvalidCredentialsException::new);

        ensureUserIsEnabled(buildingUser);

        return buildingUser;
    }

    private BuildingUser loadUserOrThrow(final Long userId) {
        return loadBuildingUserPort.loadById(userId)
                                   .orElseThrow(() -> new BuildingUserNotFoundException(userId));
    }

    private void ensureUserIsEnabled(final BuildingUser buildingUser) {
        if (!buildingUser.isEnabled()) {
            throw new InvalidCredentialsException();
        }
    }

    private void validatePassword(final String rawPassword,
                                  final String passwordHash) {
        if (!passwordEncoderPort.matches(rawPassword, passwordHash)) {
            throw new InvalidCredentialsException();
        }
    }

    private BuildingUserRole resolveRegistrationRole(final String roleValue) {
        final BuildingUserRole role = BuildingUserRole.valueOf(roleValue.toUpperCase());

        if (role == BuildingUserRole.ADMIN) {
            throw new AccessDeniedException("ADMIN role cannot be assigned during registration.");
        }

        return role;
    }

    private void validateUsernameIsUnique(final String username) {
        if (loadBuildingUserPort.existsByUsername(username)) {
            throw new DuplicateUsernameException(username);
        }
    }

    private void validateEmailIsUnique(final String email) {
        if (loadBuildingUserPort.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }
    }

    private BuildingUserProfileResult toProfileResult(final BuildingUser user,
                                                      final String preferredLanguage,
                                                      final boolean notificationsEnabled) {
        return new BuildingUserProfileResult(user.getId(),
                                             user.getUsername(),
                                             user.getEmail(),
                                             user.getDisplayName(),
                                             user.getPhoneNumber(),
                                             user.getAvatarUrl(),
                                             preferredLanguage,
                                             notificationsEnabled,
                                             user.getRole().name());
    }
}