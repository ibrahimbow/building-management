package com.why.buildingmanagement.auth.infrastructure.persistence;

import com.why.buildingmanagement.auth.domain.model.BuildingUser;
import com.why.buildingmanagement.auth.domain.model.BuildingUserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import({
        BuildingUserPersistenceAdapter.class,
        BuildingUserMapperImpl.class
})
class BuildingUserPersistenceAdapterTest {

    @Autowired
    private BuildingUserPersistenceAdapter adapter;

    @Test
    void save_shouldPersistBuildingUser() {

        final BuildingUser user = new BuildingUser(
                null,
                "ibrahim",
                "ibrahim@test.com",
                "HASHED_PASSWORD",
                "ibrahimbow",
                "+3200000000",
                "/api/files/PROFILE_AVATAR/avatar.png",
                BuildingUserRole.TENANT,
                Instant.now(),
                true);

        final BuildingUser saved =
                adapter.save(user);

        assertNotNull(saved.getId());
        assertEquals("ibrahim", saved.getUsername());
        assertEquals("ibrahim@test.com", saved.getEmail());
        assertEquals("ibrahimbow", saved.getDisplayName());
        assertEquals("+3200000000", saved.getPhoneNumber());
        assertEquals("/api/files/PROFILE_AVATAR/avatar.png", saved.getAvatarUrl());
        assertEquals(BuildingUserRole.TENANT, saved.getRole());
        assertTrue(saved.isEnabled());
    }

    @Test
    void loadById_shouldReturnBuildingUser() {

        final BuildingUser saved =
                adapter.save(newUser("ibrahim", "ibrahim@test.com", "ibrahimbow"));

        final BuildingUser found = adapter.loadById(saved.getId())
                .orElseThrow();

        assertEquals(saved.getId(), found.getId());
        assertEquals("ibrahim", found.getUsername());
    }

    @Test
    void loadByUsername_shouldReturnBuildingUser_whenUsernameExists() {

        adapter.save(newUser("ibrahim", "ibrahim@test.com", "ibrahimbow"));

        final BuildingUser found = adapter.loadByUsername("ibrahim")
                .orElseThrow();

        assertEquals("ibrahim", found.getUsername());
        assertEquals("ibrahim@test.com", found.getEmail());
    }

    @Test
    void loadByEmail_shouldReturnBuildingUser_whenEmailExists() {

        adapter.save(newUser("ibrahim", "ibrahim@test.com", "ibrahimbow"));

        final BuildingUser found = adapter.loadByEmail("ibrahim@test.com")
                .orElseThrow();

        assertEquals("ibrahim@test.com", found.getEmail());
        assertEquals("ibrahim", found.getUsername());
    }

    @Test
    void loadByUsernameOrEmail_shouldReturnUser_whenUsernameMatches() {

        adapter.save(newUser("ibrahim", "ibrahim@test.com", "ibrahimbow"));

        final BuildingUser found = adapter.loadByUsernameOrEmail("ibrahim")
                .orElseThrow();

        assertEquals("ibrahim@test.com", found.getEmail());
    }

    @Test
    void loadByUsernameOrEmail_shouldReturnUser_whenEmailMatches() {

        adapter.save(newUser("ibrahim", "ibrahim@test.com", "ibrahimbow"));

        final BuildingUser found = adapter.loadByUsernameOrEmail("ibrahim@test.com")
                .orElseThrow();

        assertEquals("ibrahim", found.getUsername());
    }

    @Test
    void existsByUsername_shouldReturnTrue_whenUsernameExists() {

        adapter.save(newUser("ibrahim", "ibrahim@test.com", "ibrahimbow"));

        assertTrue(adapter.existsByUsername("ibrahim"));
    }

    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() {

        adapter.save(newUser("ibrahim", "ibrahim@test.com", "ibrahimbow"));

        assertTrue(adapter.existsByEmail("ibrahim@test.com"));
    }

    private BuildingUser newUser(final String username,
                                 final String email,
                                 final String displayName) {

        return new BuildingUser(
                null,
                username,
                email,
                "HASHED_PASSWORD",
                displayName,
                "+3200000000",
                null,
                BuildingUserRole.TENANT,
                Instant.now(),
                true);
    }
}