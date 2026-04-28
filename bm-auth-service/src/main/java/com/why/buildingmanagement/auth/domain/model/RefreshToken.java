package com.why.buildingmanagement.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;


@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class RefreshToken {
    private final Long id;
    private final Long userId;
    private final String token;
    private final Instant expiresAt;
    private final boolean revoked;
    private final Instant createdAt;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isActive() {
        return !revoked && !isExpired();
    }

    public RefreshToken revoke() {
        return this.toBuilder()
                .revoked(true)
                .build();
    }
}
