package com.why.buildingmanagement.auth.application.port.out;

import com.why.buildingmanagement.auth.domain.model.RefreshToken;

import java.util.Optional;

public interface LoadRefreshTokenPort {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserId(Long userId);
}
