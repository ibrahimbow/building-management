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
public class RefreshTokenPersistenceAdapter implements SaveRefreshTokenPort, LoadRefreshTokenPort , DeleteRefreshTokenPort {

    private final RefreshTokenRepository repository;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return repository.findByToken(token)
                .map(this::toDomain);
    }

    @Override
    public Optional<RefreshToken> findByUserId(Long userId) {
        return repository.findByUserId(userId)
                .map(this::toDomain);
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        return toDomain(repository.save(toEntity(token)));
    }

    @Override
    public void deleteByUserId(Long userId) {
        repository.deleteByUserId(userId);
    }

    private RefreshToken toDomain(RefreshTokenEntity entity) {
        return RefreshToken.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .token(entity.getToken())
                .expiresAt(entity.getExpiresAt())
                .revoked(entity.isRevoked())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private RefreshTokenEntity toEntity(RefreshToken token) {
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