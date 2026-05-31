package com.why.buildingmanagement.auth.infrastructure.api.controller;

import com.why.buildingmanagement.auth.application.port.in.*;
import com.why.buildingmanagement.auth.application.result.LoginResult;
import com.why.buildingmanagement.auth.domain.exception.InvalidCredentialsException;
import com.why.buildingmanagement.auth.infrastructure.api.mapper.BuildingUserProfileResponseMapper;
import com.why.buildingmanagement.auth.infrastructure.security.CurrentBuildingUserService;
import com.why.buildingmanagement.auth.infrastructure.security.JwtTokenProvider;
import com.why.buildingmanagement.auth.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    private RefreshAccessTokenUseCase refreshAccessTokenUseCase;

    @MockitoBean
    private LogoutUseCase logoutUseCase;

    @MockitoBean
    private UpdateBuildingUserProfileUseCase updateBuildingUserProfileUseCase;

    @MockitoBean
    private BuildingUserProfileResponseMapper buildingUserProfileResponseMapper;

    @MockitoBean
    private CurrentBuildingUserService currentBuildingUserService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private ChangePasswordUseCase changePasswordUseCase;

    @Test
    void register_shouldReturnCreatedUserId() throws Exception {

        when(registerBuildingUserUseCase.register(
                        ArgumentMatchers.any(RegisterBuildingUserCommand.class)))
                        .thenReturn(1L);

        final String body = """
                        {
                          "username": "ibrahim",
                          "email": "ibrahim@test.com",
                          "password": "12345678",
                          "displayName": "ibrahimbow",
                          "phoneNumber": "+3200000000",
                          "role": "MANAGER"
                        }
                        """;

        mockMvc.perform(post("/api/auth/register")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(body))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").value(1));

        verify(registerBuildingUserUseCase)
                        .register(ArgumentMatchers.any(RegisterBuildingUserCommand.class));
    }

    @Test
    void register_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {

        final String body = """
                        {
                          "username": "",
                          "email": "wrong-email",
                          "password": "123",
                          "displayName": "",
                          "phoneNumber": "wrong-phone",
                          "role": ""
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

        when(loginBuildingUserUseCase.login(
                        ArgumentMatchers.any(LoginBuildingUserCommand.class)))
                        .thenReturn(new LoginResult("JWT_TOKEN", "REFRESH_TOKEN"));

        final String body = """
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

        when(loginBuildingUserUseCase.login(
                        ArgumentMatchers.any(LoginBuildingUserCommand.class)))
                        .thenThrow(new InvalidCredentialsException());

        final String body = """
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


    @Test
    @WithMockUser
    void shouldChangePassword() throws Exception {

        when(currentBuildingUserService.getCurrentUserId())
                        .thenReturn(1L);

        mockMvc.perform(patch("/api/auth/change-password")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("""
                                                        {
                                                          "currentPassword": "OldPassword123!",
                                                          "newPassword": "NewPassword123!"
                                                        }
                                                        """))
                        .andExpect(status().isNoContent());

        verify(changePasswordUseCase).changePassword(
                        new ChangePasswordCommand(
                                        1L,
                                        "OldPassword123!",
                                        "NewPassword123!"));
    }
}