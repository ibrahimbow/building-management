package com.why.buildingmanagement.auth.infrastructure.persistence;

import com.why.buildingmanagement.auth.application.port.out.DeleteRefreshTokenPort;
import com.why.buildingmanagement.auth.application.port.out.LoadRefreshTokenPort;
import com.why.buildingmanagement.auth.application.port.out.SaveRefreshTokenPort;
import com.why.buildingmanagement.auth.domain.model.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenPersistenceAdapter implements SaveRefreshTokenPort,
                                                       LoadRefreshTokenPort,
                                                       DeleteRefreshTokenPort {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public Optional<RefreshToken> findByToken(final String token) {
        return refreshTokenRepository.findByToken(token)
                                     .map(this::toDomain);
    }

    @Override
    public Optional<RefreshToken> findByUserId(final Long userId) {
        return refreshTokenRepository.findByUserId(userId)
                                     .map(this::toDomain);
    }

    @Override
    public RefreshToken save(final RefreshToken refreshToken) {
        return toDomain(refreshTokenRepository.save(toEntity(refreshToken)));
    }

    @Override
    public void deleteByUserId(final Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    private RefreshToken toDomain(final RefreshTokenEntity entity) {
        return RefreshToken.restore(entity.getId(),
                        entity.getUserId(),
                        entity.getToken(),
                        entity.getExpiresAt(),
                        entity.isRevoked(),
                        entity.getCreatedAt());
    }

    private RefreshTokenEntity toEntity(final RefreshToken token) {
        return RefreshTokenEntity.builder()
                                 .id(token.getId())
                                 .userId(token.getUserId())
                                 .token(token.getToken())
                                 .expiresAt(token.getExpiresAt())
                                 .revoked(token.isRevoked())
                                 .createdAt(token.getCreatedAt())
                                 .build();
    }


}