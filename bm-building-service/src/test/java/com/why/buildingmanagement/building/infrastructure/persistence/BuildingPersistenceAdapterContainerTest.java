package com.why.buildingmanagement.building.infrastructure.persistence;

import com.why.buildingmanagement.building.BuildingServiceApplication;
import com.why.buildingmanagement.building.application.port.out.BuildingRepositoryPort;
import com.why.buildingmanagement.building.domain.model.Building;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("Fails in local Docker environment - to be fixed")
@SpringBootTest(classes = BuildingServiceApplication.class)
@Testcontainers
class BuildingPersistenceAdapterContainerTest {

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
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

        final Optional<Building> found =
                buildingRepositoryPort.findByCode("BM-123456");

        assertThat(found).isPresent();
        assertThat(found.get().getBuildingName()).isEqualTo("Test Building");
    }

    private Building createBuilding(final String code) {
        return Building.builder()
                .id(null)
                .buildingName("Test Building")
                .code(code)
                .address("Antwerp")
                .managerName("Ibrahim")
                .managerEmail("test@test.com")
                .totalApartments(10)
                .emergencyPhone("+320000000")
                .build();
    }
}