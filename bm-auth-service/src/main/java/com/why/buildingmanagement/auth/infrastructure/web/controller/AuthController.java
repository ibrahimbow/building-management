package com.why.buildingmanagement.auth.infrastructure.web.controller;

import com.why.buildingmanagement.auth.application.port.in.LoginBuildingUserCommand;
import com.why.buildingmanagement.auth.application.port.in.LoginBuildingUserUseCase;
import com.why.buildingmanagement.auth.application.port.in.RegisterBuildingUserCommand;
import com.why.buildingmanagement.auth.application.port.in.RegisterBuildingUserUseCase;
import com.why.buildingmanagement.auth.infrastructure.web.dto.response.AuthResponse;
import com.why.buildingmanagement.auth.infrastructure.web.dto.request.LoginRequest;
import com.why.buildingmanagement.auth.infrastructure.web.dto.request.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterBuildingUserUseCase registerUserUseCase;
    private final LoginBuildingUserUseCase loginUserUseCase;

    public AuthController(RegisterBuildingUserUseCase registerUserUseCase, LoginBuildingUserUseCase loginUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
    }

    @GetMapping("/welcome")
    public String welcomeToMyFirstHomePage() {
        return "Welcome to the Homepage";
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest req) {
        Long id = registerUserUseCase.register(
                new RegisterBuildingUserCommand(req.username(), req.email(), req.password(), req.role())
        );
        return ResponseEntity.ok("User registered with id: " + id);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        String token = loginUserUseCase.login(new LoginBuildingUserCommand(req.usernameOrEmail(), req.password()));
        return ResponseEntity.ok(new AuthResponse(token, "Bearer"));
    }

}
