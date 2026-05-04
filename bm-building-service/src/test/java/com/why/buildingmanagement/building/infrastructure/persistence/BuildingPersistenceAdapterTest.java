package com.why.buildingmanagement.building.infrastructure.persistence;

import com.why.buildingmanagement.building.application.port.out.BuildingRepositoryPort;
import com.why.buildingmanagement.building.domain.model.Building;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class BuildingPersistenceAdapterTest {

    @Autowired
    private BuildingRepositoryPort buildingRepositoryPort;

    @Test
    void save_and_findByCode_shouldWork() {

        final Building building = createBuilding("BM-999999");

        final Building saved = buildingRepositoryPort.save(building);

        assertThat(saved.getId()).isNotNull();

        final Optional<Building> found =
                buildingRepositoryPort.findByCode("BM-999999");

        assertThat(found).isPresent();
        assertThat(found.get().getBuildingName()).isEqualTo("Test Building");
    }

    @Test
    void existsByCode_shouldReturnTrue_whenExists() {

        buildingRepositoryPort.save(createBuilding("BM-888888"));

        final boolean exists =
                buildingRepositoryPort.existsByCode("BM-888888");

        assertThat(exists).isTrue();
    }

    @Test
    void findByCode_shouldReturnEmpty_whenNotFound() {

        final Optional<Building> found =
                buildingRepositoryPort.findByCode("BM-NOT-EXIST");

        assertThat(found).isEmpty();
    }

    private Building createBuilding(final String code) {
        return Building.builder()
                .id(null)
                .buildingName("Test Building")
                .code(code)
                .address("Antwerp")
                .managerName("Ibrahim")
                .managerEmail("ibrahim@test.com")
                .totalApartments(10)
                .emergencyPhone("+320000000")
                .build();
    }
}