package com.why.buildingmanagement.auth.application.service;

import com.why.buildingmanagement.auth.application.port.in.LoginBuildingUserCommand;
import com.why.buildingmanagement.auth.application.port.in.RegisterBuildingUserCommand;
import com.why.buildingmanagement.auth.application.port.out.LoadBuildingUserPort;
import com.why.buildingmanagement.auth.application.port.out.SaveBuildingUserPort;
import com.why.buildingmanagement.auth.application.port.out.TokenProviderPort;
import com.why.buildingmanagement.auth.domain.model.BuildingUser;
import com.why.buildingmanagement.auth.domain.model.BuildingUserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthBuildingUserServiceTest {

    @Test
    void register_shouldSaveUser_whenUsernameAndEmailAreFree() {
        LoadBuildingUserPort loadUserPort = mock(LoadBuildingUserPort.class);
        SaveBuildingUserPort saveUserPort = mock(SaveBuildingUserPort.class);
        TokenProviderPort tokenProvider = mock(TokenProviderPort.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);

        when(loadUserPort.existsByUsername("ibrahim")).thenReturn(false);
        when(loadUserPort.existsByEmail("ibrahim@test.com")).thenReturn(false);
        when(encoder.encode("secret")).thenReturn("HASH");

        when(saveUserPort.save(any())).thenAnswer(inv -> {
            BuildingUser user = inv.getArgument(0);
            return new BuildingUser(1L, user.getUsername(), user.getEmail(), user.getPasswordHash(), user.getRole(), user.getCreatedAt(), user.isEnabled());
        });

        AuthBuildingUserService service = new AuthBuildingUserService(loadUserPort, saveUserPort, tokenProvider, encoder);

        Long id = service.register(new RegisterBuildingUserCommand("ibrahim", "ibrahim@test.com", "secret", "MANAGER"));

        assertEquals(1L, id);
        verify(saveUserPort, times(1)).save(any());
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        LoadBuildingUserPort loadUserPort = mock(LoadBuildingUserPort.class);
        SaveBuildingUserPort saveUserPort = mock(SaveBuildingUserPort.class);
        TokenProviderPort tokenProvider = mock(TokenProviderPort.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);

        BuildingUser user = new BuildingUser(1L, "ibrahim", "ib@test.com", "HASH", BuildingUserRole.MANAGER, java.time.Instant.now(), true);

        when(loadUserPort.loadByUsernameOrEmail("ibrahim")).thenReturn(Optional.of(user));
        when(encoder.matches("secret", "HASH")).thenReturn(true);
        when(tokenProvider.generateToken(user)).thenReturn("TOKEN");

        AuthBuildingUserService service = new AuthBuildingUserService(loadUserPort, saveUserPort, tokenProvider, encoder);

        String token = service.login(new LoginBuildingUserCommand("ibrahim", "secret"));

        assertEquals("TOKEN", token);
    }

}