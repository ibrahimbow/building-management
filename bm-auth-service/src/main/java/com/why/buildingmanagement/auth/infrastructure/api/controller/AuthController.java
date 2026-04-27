package com.why.buildingmanagement.auth.infrastructure.api.controller;

import com.why.buildingmanagement.auth.application.port.in.LoginBuildingUserCommand;
import com.why.buildingmanagement.auth.application.port.in.LoginBuildingUserUseCase;
import com.why.buildingmanagement.auth.application.port.in.RegisterBuildingUserCommand;
import com.why.buildingmanagement.auth.application.port.in.RegisterBuildingUserUseCase;
import com.why.buildingmanagement.auth.infrastructure.api.dto.response.RegisterResponse;
import com.why.buildingmanagement.auth.infrastructure.security.JwtTokenProvider;
import com.why.buildingmanagement.auth.infrastructure.api.dto.request.LoginRequest;
import com.why.buildingmanagement.auth.infrastructure.api.dto.request.RegisterRequest;
import com.why.buildingmanagement.auth.infrastructure.api.dto.response.AuthResponse;
import com.why.buildingmanagement.auth.infrastructure.api.dto.response.CurrentBuildingUserResponse;
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
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(RegisterBuildingUserUseCase registerUserUseCase, LoginBuildingUserUseCase loginUserUseCase, JwtTokenProvider jwtTokenProvider) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/welcome")
    public String welcomeToMyFirstHomePage() {
        return "Welcome to the Homepage";
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest req) {
        Long id = registerUserUseCase.register(
                new RegisterBuildingUserCommand(req.username(),
                                                req.email(),
                                                req.password()));
        return ResponseEntity.ok(new RegisterResponse(id));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        String token = loginUserUseCase.login(new LoginBuildingUserCommand(req.usernameOrEmail(), req.password()));
        return ResponseEntity.ok(new AuthResponse(token, "Bearer"));
    }

    @GetMapping("/profile")
    public CurrentBuildingUserResponse me(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        String token = authorizationHeader.substring(7);

        if (!jwtTokenProvider.isTokenValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        return new CurrentBuildingUserResponse(
                jwtTokenProvider.getUserId(token),
                jwtTokenProvider.getUsername(token),
                jwtTokenProvider.getEmail(token),
                jwtTokenProvider.getRole(token)
        );
    }

}
