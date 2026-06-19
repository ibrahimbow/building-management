package com.why.buildingmanagement.auth.infrastructure.persistence;

import com.why.buildingmanagement.auth.domain.model.BuildingUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static com.why.buildingmanagement.auth.domain.model.BuildingUserRole.TENANT;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({BuildingUserPersistenceAdapter.class, BuildingUserMapperImpl.class})
class BuildingUserPersistenceAdapterTest {

    private static final String USERNAME = "ibrahim";
    private static final String EMAIL = "ibrahim@test.com";
    private static final String PASSWORD_HASH = "HASHED_PASSWORD";
    private static final String DISPLAY_NAME = "ibrahimbow";
    private static final String PHONE_NUMBER = "+3200000000";
    private static final String AVATAR_URL = "/api/files/PROFILE_AVATAR/avatar.png";
    private static final Instant CREATED_AT = Instant.parse("2026-06-18T12:00:00Z");

    @Autowired
    private BuildingUserPersistenceAdapter adapter;

    @Test
    void save_shouldPersistBuildingUser() {
        final BuildingUser user = BuildingUser.restore(null,
                                                       USERNAME,
                                                       EMAIL,
                                                       PASSWORD_HASH,
                                                       DISPLAY_NAME,
                                                       PHONE_NUMBER,
                                                       AVATAR_URL,
                                                       TENANT,
                                                       CREATED_AT,
                                                       true);

        final BuildingUser saved = adapter.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo(USERNAME);
        assertThat(saved.getEmail()).isEqualTo(EMAIL);
        assertThat(saved.getPasswordHash()).isEqualTo(PASSWORD_HASH);
        assertThat(saved.getDisplayName()).isEqualTo(DISPLAY_NAME);
        assertThat(saved.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
        assertThat(saved.getAvatarUrl()).isEqualTo(AVATAR_URL);
        assertThat(saved.getRole()).isEqualTo(TENANT);
        assertThat(saved.getCreatedAt()).isEqualTo(CREATED_AT);
        assertThat(saved.isEnabled()).isTrue();
    }

    @Test
    void loadById_shouldReturnBuildingUser_whenUserExists() {
        final BuildingUser saved = adapter.save(newUser(USERNAME,
                                                        EMAIL,
                                                        DISPLAY_NAME));

        final BuildingUser found = adapter.loadById(saved.getId())
                                          .orElseThrow();

        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getUsername()).isEqualTo(USERNAME);
        assertThat(found.getEmail()).isEqualTo(EMAIL);
    }

    @Test
    void loadById_shouldReturnEmpty_whenUserDoesNotExist() {
        assertThat(adapter.loadById(999L)).isEmpty();
    }

    @Test
    void loadByUsername_shouldReturnBuildingUser_whenUsernameExists() {
        adapter.save(newUser(USERNAME, EMAIL, DISPLAY_NAME));

        final BuildingUser found = adapter.loadByUsername(USERNAME)
                                          .orElseThrow();

        assertThat(found.getUsername()).isEqualTo(USERNAME);
        assertThat(found.getEmail()).isEqualTo(EMAIL);
    }

    @Test
    void loadByUsername_shouldReturnEmpty_whenUsernameDoesNotExist() {
        assertThat(adapter.loadByUsername("unknown")).isEmpty();
    }

    @Test
    void loadByEmail_shouldReturnBuildingUser_whenEmailExists() {
        adapter.save(newUser(USERNAME, EMAIL, DISPLAY_NAME));

        final BuildingUser found = adapter.loadByEmail(EMAIL)
                                          .orElseThrow();

        assertThat(found.getEmail()).isEqualTo(EMAIL);
        assertThat(found.getUsername()).isEqualTo(USERNAME);
    }

    @Test
    void loadByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
        assertThat(adapter.loadByEmail("unknown@test.com")).isEmpty();
    }

    @Test
    void loadByUsernameOrEmail_shouldReturnUser_whenUsernameMatches() {
        adapter.save(newUser(USERNAME, EMAIL, DISPLAY_NAME));

        final BuildingUser found = adapter.loadByUsernameOrEmail(USERNAME)
                                          .orElseThrow();

        assertThat(found.getUsername()).isEqualTo(USERNAME);
        assertThat(found.getEmail()).isEqualTo(EMAIL);
    }

    @Test
    void loadByUsernameOrEmail_shouldReturnUser_whenEmailMatches() {
        adapter.save(newUser(USERNAME, EMAIL, DISPLAY_NAME));

        final BuildingUser found = adapter.loadByUsernameOrEmail(EMAIL)
                                          .orElseThrow();

        assertThat(found.getUsername()).isEqualTo(USERNAME);
        assertThat(found.getEmail()).isEqualTo(EMAIL);
    }

    @Test
    void loadByUsernameOrEmail_shouldReturnEmpty_whenNoMatchExists() {
        assertThat(adapter.loadByUsernameOrEmail("unknown")).isEmpty();
    }

    @Test
    void existsByUsername_shouldReturnTrue_whenUsernameExists() {
        adapter.save(newUser(USERNAME, EMAIL, DISPLAY_NAME));

        assertThat(adapter.existsByUsername(USERNAME)).isTrue();
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenUsernameDoesNotExist() {
        assertThat(adapter.existsByUsername("unknown")).isFalse();
    }

    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        adapter.save(newUser(USERNAME, EMAIL, DISPLAY_NAME));

        assertThat(adapter.existsByEmail(EMAIL)).isTrue();
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {
        assertThat(adapter.existsByEmail("unknown@test.com")).isFalse();
    }

    private BuildingUser newUser(final String username,
                                 final String email,
                                 final String displayName) {
        return BuildingUser.restore(null,
                                    username,
                                    email,
                                    PASSWORD_HASH,
                                    displayName,
                                    PHONE_NUMBER,
                                    null,
                                    TENANT,
                                    CREATED_AT,
                                    true);
    }
}