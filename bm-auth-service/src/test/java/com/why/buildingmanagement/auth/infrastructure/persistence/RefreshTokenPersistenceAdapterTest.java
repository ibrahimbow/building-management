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
        RefreshToken token = RefreshToken.builder()
                .userId(1L)
                .token("REFRESH_TOKEN_123")
                .expiresAt(Instant.now().plusSeconds(3600))
                .revoked(false)
                .createdAt(Instant.now())
                .build();

        RefreshToken saved = adapter.save(token);

        assertNotNull(saved.getId());
        assertEquals(1L, saved.getUserId());
        assertEquals("REFRESH_TOKEN_123", saved.getToken());
        assertFalse(saved.isRevoked());
    }

    @Test
    void findByToken_shouldReturnRefreshToken() {
        RefreshToken token = RefreshToken.builder()
                .userId(1L)
                .token("REFRESH_TOKEN_456")
                .expiresAt(Instant.now().plusSeconds(3600))
                .revoked(false)
                .createdAt(Instant.now())
                .build();

        adapter.save(token);

        RefreshToken found = adapter.findByToken("REFRESH_TOKEN_456")
                .orElseThrow();

        assertEquals(1L, found.getUserId());
        assertEquals("REFRESH_TOKEN_456", found.getToken());
    }

    @Test
    void findByUserId_shouldReturnRefreshToken() {
        RefreshToken token = RefreshToken.builder()
                .userId(10L)
                .token("REFRESH_TOKEN_USER")
                .expiresAt(Instant.now().plusSeconds(3600))
                .revoked(false)
                .createdAt(Instant.now())
                .build();

        adapter.save(token);

        RefreshToken found = adapter.findByUserId(10L)
                .orElseThrow();

        assertEquals("REFRESH_TOKEN_USER", found.getToken());
    }

    @Test
    void deleteByUserId_shouldDeleteRefreshToken() {
        RefreshToken token = RefreshToken.builder()
                .userId(1L)
                .token("REFRESH_TOKEN_789")
                .expiresAt(Instant.now().plusSeconds(3600))
                .revoked(false)
                .createdAt(Instant.now())
                .build();

        adapter.save(token);

        adapter.deleteByUserId(1L);

        assertTrue(adapter.findByToken("REFRESH_TOKEN_789").isEmpty());
    }
}