package com.why.buildingmanagement.auth.application.service;

import com.why.buildingmanagement.auth.application.port.out.DeleteRefreshTokenPort;
import com.why.buildingmanagement.auth.application.port.out.LoadRefreshTokenPort;
import com.why.buildingmanagement.auth.application.port.out.SaveRefreshTokenPort;
import com.why.buildingmanagement.auth.domain.model.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private static final Duration REFRESH_TOKEN_VALIDITY = Duration.ofDays(7); // 7 days

    private final SaveRefreshTokenPort saveRefreshTokenPort;
    private final LoadRefreshTokenPort loadRefreshTokenPort;
    private final DeleteRefreshTokenPort deleteRefreshTokenPort;

    public RefreshToken createForUser(Long userId) {
        Instant now = Instant.now();
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token(UUID.randomUUID().toString())
                .expiresAt(now.plus(REFRESH_TOKEN_VALIDITY))
                .revoked(false)
                .createdAt(now)
                .build();

        return saveRefreshTokenPort.save(refreshToken);
    }

    public void deleteForUser(Long userId) {
        deleteRefreshTokenPort.deleteByUserId(userId);
    }


    public RefreshToken validate(String token) {
        RefreshToken refreshToken = loadRefreshTokenPort.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (!refreshToken.isActive()) {
            throw new IllegalArgumentException("Refresh token is expired or revoked");
        }

        return refreshToken;
    }
}
