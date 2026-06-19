package com.why.buildingmanagement.auth.infrastructure.persistence;

import com.why.buildingmanagement.auth.domain.model.RefreshToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(RefreshTokenPersistenceAdapter.class)
class RefreshTokenPersistenceAdapterTest {

    private static final Long USER_ID = 1L;
    private static final String REFRESH_TOKEN_VALUE = "REFRESH_TOKEN_123";
    private static final Instant CREATED_AT = Instant.parse("2026-06-18T12:00:00Z");
    private static final Instant EXPIRES_AT = Instant.parse("2026-06-25T12:00:00Z");

    @Autowired
    private RefreshTokenPersistenceAdapter adapter;

    @Test
    void save_shouldPersistRefreshToken() {
        final RefreshToken saved = adapter.save(refreshToken(USER_ID, REFRESH_TOKEN_VALUE));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(USER_ID);
        assertThat(saved.getToken()).isEqualTo(REFRESH_TOKEN_VALUE);
        assertThat(saved.getExpiresAt()).isEqualTo(EXPIRES_AT);
        assertThat(saved.isRevoked()).isFalse();
        assertThat(saved.getCreatedAt()).isEqualTo(CREATED_AT);
    }

    @Test
    void findByToken_shouldReturnRefreshToken_whenTokenExists() {
        adapter.save(refreshToken(USER_ID, "REFRESH_TOKEN_456"));

        final RefreshToken found = adapter.findByToken("REFRESH_TOKEN_456")
                                          .orElseThrow();

        assertThat(found.getUserId()).isEqualTo(USER_ID);
        assertThat(found.getToken()).isEqualTo("REFRESH_TOKEN_456");
        assertThat(found.isRevoked()).isFalse();
    }

    @Test
    void findByToken_shouldReturnEmpty_whenTokenDoesNotExist() {
        assertThat(adapter.findByToken("UNKNOWN_TOKEN")).isEmpty();
    }

    @Test
    void findByUserId_shouldReturnRefreshToken_whenUserHasToken() {
        adapter.save(refreshToken(10L, "REFRESH_TOKEN_USER"));

        final RefreshToken found = adapter.findByUserId(10L)
                                          .orElseThrow();

        assertThat(found.getUserId()).isEqualTo(10L);
        assertThat(found.getToken()).isEqualTo("REFRESH_TOKEN_USER");
    }

    @Test
    void findByUserId_shouldReturnEmpty_whenUserHasNoToken() {
        assertThat(adapter.findByUserId(999L)).isEmpty();
    }

    @Test
    void deleteByUserId_shouldDeleteRefreshToken() {
        adapter.save(refreshToken(USER_ID, "REFRESH_TOKEN_789"));

        adapter.deleteByUserId(USER_ID);

        assertThat(adapter.findByToken("REFRESH_TOKEN_789")).isEmpty();
        assertThat(adapter.findByUserId(USER_ID)).isEmpty();
    }

    private RefreshToken refreshToken(final Long userId,
                                      final String token) {
        return RefreshToken.restore(null,
                        userId,
                        token,
                        EXPIRES_AT,
                        false,
                        CREATED_AT);
    }
}