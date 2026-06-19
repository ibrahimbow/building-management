package com.why.buildingmanagement.auth.application.service;

import com.why.buildingmanagement.auth.application.port.out.DeleteRefreshTokenPort;
import com.why.buildingmanagement.auth.application.port.out.LoadRefreshTokenPort;
import com.why.buildingmanagement.auth.application.port.out.SaveRefreshTokenPort;
import com.why.buildingmanagement.auth.domain.exception.InvalidRefreshTokenException;
import com.why.buildingmanagement.auth.domain.model.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private static final Duration REFRESH_TOKEN_VALIDITY = Duration.ofDays(7);

    private final SaveRefreshTokenPort saveRefreshTokenPort;
    private final LoadRefreshTokenPort loadRefreshTokenPort;
    private final DeleteRefreshTokenPort deleteRefreshTokenPort;

    @Transactional
    public RefreshToken createForUser(final Long userId) {
        Objects.requireNonNull(userId, "userId must not be null");

        final Instant expiresAt = Instant.now().plus(REFRESH_TOKEN_VALIDITY);

        final RefreshToken refreshToken = RefreshToken.createNew(userId,
                                                                 UUID.randomUUID().toString(),
                                                                 expiresAt);

        return saveRefreshTokenPort.save(refreshToken);
    }

    @Transactional
    public void deleteForUser(final Long userId) {
        Objects.requireNonNull(userId, "userId must not be null");

        deleteRefreshTokenPort.deleteByUserId(userId);
    }

    public RefreshToken validate(final String token) {
        Objects.requireNonNull(token, "refresh token must not be null");

        final RefreshToken refreshToken = loadRefreshTokenPort.findByToken(token)
                                                              .orElseThrow(() -> new InvalidRefreshTokenException(
                                                                              "Invalid refresh token"));

        if (!refreshToken.isActive()) {
            throw new InvalidRefreshTokenException("Refresh token is expired or revoked");
        }

        return refreshToken;
    }
}