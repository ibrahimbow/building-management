package com.why.buildingmanagement.auth.infrastructure.api.controller;

import com.why.buildingmanagement.auth.application.port.in.*;
import com.why.buildingmanagement.auth.application.result.LoginResult;
import com.why.buildingmanagement.auth.infrastructure.api.dto.request.LoginRequest;
import com.why.buildingmanagement.auth.infrastructure.api.dto.request.RefreshTokenRequest;
import com.why.buildingmanagement.auth.infrastructure.api.dto.request.RegisterRequest;
import com.why.buildingmanagement.auth.infrastructure.api.dto.response.AuthResponse;
import com.why.buildingmanagement.auth.infrastructure.api.dto.response.CurrentBuildingUserResponse;
import com.why.buildingmanagement.auth.infrastructure.api.dto.response.RegisterResponse;
import com.why.buildingmanagement.auth.infrastructure.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterBuildingUserUseCase registerUserUseCase;
    private final LoginBuildingUserUseCase loginUserUseCase;
    private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(final RegisterBuildingUserUseCase registerUserUseCase,
                          final LoginBuildingUserUseCase loginUserUseCase,
                          final RefreshAccessTokenUseCase refreshAccessTokenUseCase,
                          final LogoutUseCase logoutUseCase,
                          final JwtTokenProvider jwtTokenProvider) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.refreshAccessTokenUseCase = refreshAccessTokenUseCase;
        this.logoutUseCase = logoutUseCase;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/welcome")
    public String welcomeToMyFirstHomePage() {
        return "Welcome to the Homepage";
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody final RegisterRequest request) {
        final Long id = registerUserUseCase.register(
                new RegisterBuildingUserCommand(request.username(),
                        request.email(),
                        request.password()));
        return ResponseEntity.ok(new RegisterResponse(id));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody final LoginRequest request) {

        final LoginResult result = loginUserUseCase.login(
                new LoginBuildingUserCommand(
                        request.usernameOrEmail(),
                        request.password()));

        return ResponseEntity.ok(
                new AuthResponse(
                        result.accessToken(),
                        result.refreshToken(),
                        "Bearer"));
    }

    @GetMapping("/profile")
    public CurrentBuildingUserResponse me(@RequestHeader("Authorization") final String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        final String token = authorizationHeader.substring(7);

        if (!jwtTokenProvider.isTokenValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        return new CurrentBuildingUserResponse(
                jwtTokenProvider.getUserId(token),
                jwtTokenProvider.getUsername(token),
                jwtTokenProvider.getEmail(token),
                jwtTokenProvider.getRole(token));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody final RefreshTokenRequest request) {

        final LoginResult result = refreshAccessTokenUseCase.refresh(
                new RefreshAccessTokenCommand(request.refreshToken()));

        return ResponseEntity.ok(
                new AuthResponse(
                        result.accessToken(),
                        result.refreshToken(),
                        "Bearer"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody final RefreshTokenRequest request) {
        logoutUseCase.logout(new LogoutCommand(request.refreshToken()));

        return ResponseEntity.noContent().build();
    }
}
