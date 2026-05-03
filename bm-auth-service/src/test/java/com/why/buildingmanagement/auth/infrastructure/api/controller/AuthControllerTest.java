package com.why.buildingmanagement.auth.infrastructure.api.controller;

import com.why.buildingmanagement.auth.application.port.in.*;
import com.why.buildingmanagement.auth.application.result.LoginResult;
import com.why.buildingmanagement.auth.domain.exception.InvalidCredentialsException;
import com.why.buildingmanagement.auth.infrastructure.security.JwtTokenProvider;
import com.why.buildingmanagement.auth.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterBuildingUserUseCase registerBuildingUserUseCase;

    @MockitoBean
    private LoginBuildingUserUseCase loginBuildingUserUseCase;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private RefreshAccessTokenUseCase refreshAccessTokenUseCase;

    @MockitoBean
    private LogoutUseCase logoutUseCase;

    @Test
    void register_shouldReturnCreatedUserId() throws Exception {
        when(registerBuildingUserUseCase.register(ArgumentMatchers.any(RegisterBuildingUserCommand.class)))
                .thenReturn(1L);

        String body = """
                {
                  "username": "ibrahim",
                  "email": "ibrahim@test.com",
                  "password": "12345678",
                  "nickname": "ibrahimbow"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(registerBuildingUserUseCase)
                .register(ArgumentMatchers.any(RegisterBuildingUserCommand.class));
    }

    @Test
    void register_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
        String body = """
                {
                  "username": "",
                  "email": "wrong-email",
                  "password": "123",
                  "nickname": ""
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));
    }

    @Test
    void login_shouldReturnAccessAndRefreshToken() throws Exception {
        when(loginBuildingUserUseCase.login(ArgumentMatchers.any(LoginBuildingUserCommand.class)))
                .thenReturn(new LoginResult("JWT_TOKEN", "REFRESH_TOKEN"));

        String body = """
                {
                  "usernameOrEmail": "ibrahim",
                  "password": "12345678"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("JWT_TOKEN"))
                .andExpect(jsonPath("$.refreshToken").value("REFRESH_TOKEN"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        verify(loginBuildingUserUseCase)
                .login(ArgumentMatchers.any(LoginBuildingUserCommand.class));
    }

    @Test
    void login_shouldReturnUnauthorized_whenCredentialsAreInvalid() throws Exception {
        when(loginBuildingUserUseCase.login(ArgumentMatchers.any(LoginBuildingUserCommand.class)))
                .thenThrow(new InvalidCredentialsException());

        String body = """
                {
                  "usernameOrEmail": "ibrahim",
                  "password": "wrong-password"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("INVALID_CREDENTIALS"));
    }

}