package com.why.buildingmanagement.auth.infrastructure.persistence;

import com.why.buildingmanagement.auth.domain.model.RefreshToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(RefreshTokenPersistenceAdapter.class)
class RefreshTokenPersistenceAdapterTest {

    @Autowired
    private RefreshTokenPersistenceAdapter adapter;

    @Test
    void save_shouldPersistRefreshToken() {

        final RefreshToken saved =
                adapter.save(refreshToken(1L, "REFRESH_TOKEN_123"));

        assertNotNull(saved.getId());
        assertEquals(1L, saved.getUserId());
        assertEquals("REFRESH_TOKEN_123", saved.getToken());
        assertFalse(saved.isRevoked());
    }

    @Test
    void findByToken_shouldReturnRefreshToken() {

        adapter.save(refreshToken(1L, "REFRESH_TOKEN_456"));

        final RefreshToken found = adapter.findByToken("REFRESH_TOKEN_456")
                .orElseThrow();

        assertEquals(1L, found.getUserId());
        assertEquals("REFRESH_TOKEN_456", found.getToken());
    }

    @Test
    void findByUserId_shouldReturnRefreshToken() {

        adapter.save(refreshToken(10L, "REFRESH_TOKEN_USER"));

        final RefreshToken found = adapter.findByUserId(10L)
                .orElseThrow();

        assertEquals("REFRESH_TOKEN_USER", found.getToken());
    }

    @Test
    void deleteByUserId_shouldDeleteRefreshToken() {

        adapter.save(refreshToken(1L, "REFRESH_TOKEN_789"));

        adapter.deleteByUserId(1L);

        assertTrue(adapter.findByToken("REFRESH_TOKEN_789").isEmpty());
    }

    private RefreshToken refreshToken(final Long userId,
                                      final String token) {

        return RefreshToken.builder()
                .userId(userId)
                .token(token)
                .expiresAt(Instant.now().plusSeconds(3600))
                .revoked(false)
                .createdAt(Instant.now())
                .build();
    }
}