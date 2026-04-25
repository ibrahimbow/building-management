package com.why.buildingmanagement.auth.application.service;

import com.why.buildingmanagement.auth.application.port.in.LoginBuildingUserCommand;
import com.why.buildingmanagement.auth.application.port.in.RegisterBuildingUserCommand;
import com.why.buildingmanagement.auth.application.port.out.LoadBuildingUserPort;
import com.why.buildingmanagement.auth.application.port.out.SaveBuildingUserPort;
import com.why.buildingmanagement.auth.application.port.out.TokenProviderPort;
import com.why.buildingmanagement.auth.domain.model.BuildingUser;
import com.why.buildingmanagement.auth.domain.model.BuildingUserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
            BuildingUser buildingUser = inv.getArgument(0);
            return new BuildingUser(1L, buildingUser.getUsername(), buildingUser.getEmail(), buildingUser.getPasswordHash(), buildingUser.getRole(), buildingUser.getCreatedAt(), buildingUser.isEnabled());
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

        BuildingUser buildingUser = new BuildingUser(1L, "ibrahim", "ib@test.com", "HASH", BuildingUserRole.MANAGER, java.time.Instant.now(), true);

        when(loadUserPort.loadByUsernameOrEmail("ibrahim")).thenReturn(Optional.of(buildingUser));
        when(encoder.matches("secret", "HASH")).thenReturn(true);
        when(tokenProvider.generateToken(buildingUser)).thenReturn("TOKEN");

        AuthBuildingUserService service = new AuthBuildingUserService(loadUserPort, saveUserPort, tokenProvider, encoder);

        String token = service.login(new LoginBuildingUserCommand("ibrahim", "secret"));

        assertEquals("TOKEN", token);
    }

}