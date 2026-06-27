package com.why.buildingmanagement.auth.infrastructure.api.controller;

import com.why.buildingmanagement.auth.application.port.in.*;
import com.why.buildingmanagement.auth.application.result.BuildingUserProfileResult;
import com.why.buildingmanagement.auth.application.result.LoginResult;
import com.why.buildingmanagement.auth.infrastructure.api.dto.request.*;
import com.why.buildingmanagement.auth.infrastructure.api.dto.response.AuthResponse;
import com.why.buildingmanagement.auth.infrastructure.api.dto.response.BuildingUserProfileResponse;
import com.why.buildingmanagement.auth.infrastructure.api.dto.response.RegisterResponse;
import com.why.buildingmanagement.auth.infrastructure.api.mapper.BuildingUserProfileResponseMapper;
import com.why.buildingmanagement.auth.infrastructure.security.CurrentBuildingUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterBuildingUserUseCase registerUserUseCase;
    private final LoginBuildingUserUseCase loginUserUseCase;
    private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final UpdateBuildingUserProfileUseCase updateBuildingUserProfileUseCase;
    private final BuildingUserProfileResponseMapper buildingUserProfileResponseMapper;
    private final CurrentBuildingUserService currentBuildingUserService;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final GetBuildingUserProfileUseCase getBuildingUserProfileUseCase;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody final RegisterRequest request) {

        final Long id = registerUserUseCase.register(new RegisterBuildingUserCommand(request.username(),
                                                                                     request.email(),
                                                                                     request.password(),
                                                                                     request.displayName(),
                                                                                     request.phoneNumber(),
                                                                                     request.role().toUpperCase()));

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new RegisterResponse(id));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody final LoginRequest request) {

        final LoginResult result = loginUserUseCase
                        .login(new LoginBuildingUserCommand(request.usernameOrEmail(), request.password()));

        return ResponseEntity.ok(new AuthResponse(
                        result.accessToken(),
                        result.refreshToken(),
                        "Bearer"));
    }

    @GetMapping("/profile")
    public ResponseEntity<BuildingUserProfileResponse> profile() {

        final Long userId = currentBuildingUserService.getCurrentUserId();

        final BuildingUserProfileResult result = getBuildingUserProfileUseCase.getProfile(userId);

        return ResponseEntity.ok(buildingUserProfileResponseMapper.toResponse(result));
    }

    @PutMapping("/profile")
    public ResponseEntity<BuildingUserProfileResponse> updateProfile(@Valid @RequestBody final UpdateBuildingUserProfileRequest request) {

        final Long userId = currentBuildingUserService.getCurrentUserId();

        final BuildingUserProfileResult result = updateBuildingUserProfileUseCase
                        .updateProfile(new UpdateBuildingUserProfileCommand(userId,
                                                                            request.displayName(),
                                                                            request.phoneNumber(),
                                                                            request.avatarUrl(),
                                                                            request.preferredLanguage(),
                                                                            request.notificationsEnabled()));

        return ResponseEntity.ok(buildingUserProfileResponseMapper.toResponse(result));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody final RefreshTokenRequest request) {

        final LoginResult result = refreshAccessTokenUseCase.refresh(new RefreshAccessTokenCommand(request.refreshToken()));

        return ResponseEntity.ok(new AuthResponse(result.accessToken(), result.refreshToken(), "Bearer"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody final RefreshTokenRequest request) {

        logoutUseCase.logout(new LogoutCommand(request.refreshToken()));

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody final ChangePasswordRequest request) {

        final Long userId = currentBuildingUserService.getCurrentUserId();

        changePasswordUseCase.changePassword(new ChangePasswordCommand(userId,
                                                                       request.currentPassword(),
                                                                       request.newPassword()));

        return ResponseEntity.noContent().build();
    }
}