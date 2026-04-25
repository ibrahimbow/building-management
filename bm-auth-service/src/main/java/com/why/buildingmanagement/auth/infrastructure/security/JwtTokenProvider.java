package com.why.buildingmanagement.auth.infrastructure.security;

import com.why.buildingmanagement.auth.application.port.out.TokenProviderPort;
import com.why.buildingmanagement.auth.domain.model.BuildingUser;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider implements TokenProviderPort {

    @Override
    public String generateToken(BuildingUser buildingUser) {
        return "FAKE_TOKEN_FOR_" + buildingUser.getUsername();
    }

}
