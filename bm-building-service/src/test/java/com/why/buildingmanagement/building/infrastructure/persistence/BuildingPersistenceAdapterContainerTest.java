package com.why.buildingmanagement.building.infrastructure.persistence;

import com.why.buildingmanagement.building.BuildingServiceApplication;
import com.why.buildingmanagement.building.application.port.out.BuildingRepositoryPort;
import com.why.buildingmanagement.building.domain.model.Building;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = BuildingServiceApplication.class)
@Testcontainers
class BuildingPersistenceAdapterContainerTest {

    @Container
    static final org.testcontainers.postgresql.PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16")
                    .withDatabaseName("building_test_db")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configure(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private BuildingRepositoryPort buildingRepositoryPort;

    @Test
    void save_and_find_should_work() {
        final Building building = createBuilding("BM-123456");

        final Building saved = buildingRepositoryPort.save(building);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getManagerId()).isEqualTo(12L);

        final Optional<Building> found =
                buildingRepositoryPort.findByCode("BM-123456");

        assertThat(found).isPresent();
        assertThat(found.get().getBuildingName()).isEqualTo("Test Building");
        assertThat(found.get().getManagerId()).isEqualTo(12L);
    }

    private Building createBuilding(final String code) {
        return Building.createNew(
                "Test Building",
                code,
                "Antwerp",
                12L,
                10,
                "+320000000");
    }
}