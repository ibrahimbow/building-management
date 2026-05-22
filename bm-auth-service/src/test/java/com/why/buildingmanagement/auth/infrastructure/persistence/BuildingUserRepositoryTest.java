package com.why.buildingmanagement.auth.infrastructure.persistence;

import com.why.buildingmanagement.auth.domain.model.BuildingUserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
class BuildingUserRepositoryTest {

    @Container
    static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16")
                    .withDatabaseName("building_test_db")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void postgresProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private BuildingUserRepository repository;

    @Test
    void save_shouldPersistBuildingUser() {
        final BuildingUserEntity saved = repository.save(userEntity());

        assertNotNull(saved.getId());
        assertEquals("ibrahim", saved.getUsername());
        assertEquals("ibrahim@test.com", saved.getEmail());
        assertEquals("ibrahimbow", saved.getDisplayName());
        assertEquals("+3200000000", saved.getPhoneNumber());
        assertNull(saved.getAvatarUrl());
        assertEquals("HASHED_PASSWORD", saved.getPasswordHash());
        assertEquals(BuildingUserRole.TENANT.name(), saved.getRole());
        assertTrue(saved.isEnabled());
    }

    @Test
    void findByUsername_shouldReturnUser_whenUsernameExists() {
        repository.save(userEntity());

        final Optional<BuildingUserEntity> result = repository.findByUsername("ibrahim");

        assertTrue(result.isPresent());
        assertEquals("ibrahim", result.get().getUsername());
    }

    @Test
    void findByEmail_shouldReturnUser_whenEmailExists() {
        repository.save(userEntity());

        final Optional<BuildingUserEntity> result = repository.findByEmail("ibrahim@test.com");

        assertTrue(result.isPresent());
        assertEquals("ibrahim@test.com", result.get().getEmail());
    }

    @Test
    void findByUsernameOrEmail_shouldReturnUser_whenUsernameExists() {
        repository.save(userEntity());

        final Optional<BuildingUserEntity> result =
                repository.findByUsernameOrEmail("ibrahim", "ibrahim");

        assertTrue(result.isPresent());
        assertEquals("ibrahim", result.get().getUsername());
    }

    @Test
    void findByUsernameOrEmail_shouldReturnUser_whenEmailExists() {
        repository.save(userEntity());

        final Optional<BuildingUserEntity> result =
                repository.findByUsernameOrEmail("ibrahim@test.com", "ibrahim@test.com");

        assertTrue(result.isPresent());
        assertEquals("ibrahim@test.com", result.get().getEmail());
    }

    @Test
    void existsByUsername_shouldReturnTrue_whenUsernameExists() {
        repository.save(userEntity());

        assertTrue(repository.existsByUsername("ibrahim"));
    }

    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        repository.save(userEntity());

        assertTrue(repository.existsByEmail("ibrahim@test.com"));
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenUsernameDoesNotExist() {
        assertFalse(repository.existsByUsername("ghost"));
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {
        assertFalse(repository.existsByEmail("ghost@test.com"));
    }

    private BuildingUserEntity userEntity() {
        return new BuildingUserEntity(
                null,
                "ibrahim",
                "ibrahim@test.com",
                "HASHED_PASSWORD",
                "ibrahimbow",
                "+3200000000",
                null,
                BuildingUserRole.TENANT.name(),
                true,
                Instant.now());
    }
}