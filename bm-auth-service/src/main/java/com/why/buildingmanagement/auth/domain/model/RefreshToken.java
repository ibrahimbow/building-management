package com.why.buildingmanagement.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;


@Getter
public class RefreshToken {

    private final Long id;
    private final Long userId;
    private final String token;
    private final Instant expiresAt;
    private final boolean revoked;
    private final Instant createdAt;

    private RefreshToken(final Long id,
                         final Long userId,
                         final String token,
                         final Instant expiresAt,
                         final boolean revoked,
                         final Instant createdAt) {

        this.id = id;
        this.userId = userId;
        this.token = token;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.createdAt = createdAt;
    }

    public static RefreshToken createNew(final Long userId, final String token, final Instant expiresAt) {

        return new RefreshToken(null,
                                userId,
                                token,
                                expiresAt,
                                false,
                                Instant.now());
    }

    public static RefreshToken restore(final Long id,
                                       final Long userId,
                                       final String token,
                                       final Instant expiresAt,
                                       final boolean revoked,
                                       final Instant createdAt) {

        return new RefreshToken(id,
                                userId,
                                token,
                                expiresAt,
                                revoked,
                                createdAt);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isActive() {
        return !revoked && !isExpired();
    }

    public RefreshToken revoke() {
        return new RefreshToken(id,
                                userId,
                                token,
                                expiresAt,
                                true,
                                createdAt);
    }
}
