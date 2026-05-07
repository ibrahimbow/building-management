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
        final Building building = createBuilding("BM-999999", 12L);

        final Building saved = buildingRepositoryPort.save(building);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getManagerId()).isEqualTo(12L);

        final Optional<Building> found =
                buildingRepositoryPort.findByCode("BM-999999");

        assertThat(found).isPresent();
        assertThat(found.get().getBuildingName()).isEqualTo("Test Building");
        assertThat(found.get().getManagerId()).isEqualTo(12L);
    }

    @Test
    void existsByCode_shouldReturnTrue_whenExists() {
        buildingRepositoryPort.save(createBuilding("BM-888888", 13L));

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

    @Test
    void findByManagerId_shouldReturnBuildingsForManager() {
        final Long managerId = 99L;

        buildingRepositoryPort.save(createBuilding("BM-777777", managerId));
        buildingRepositoryPort.save(createBuilding("BM-666666", managerId));
        buildingRepositoryPort.save(createBuilding("BM-555555", 100L));

        final var buildings = buildingRepositoryPort.findByManagerId(managerId);

        assertThat(buildings).hasSize(2);
        assertThat(buildings)
                .extracting(Building::getManagerId)
                .containsOnly(managerId);

        assertThat(buildings)
                .extracting(Building::getCode)
                .containsExactlyInAnyOrder("BM-777777", "BM-666666");
    }

    private Building createBuilding(
            final String code,
            final Long managerId
    ) {
        return Building.createNew(
                "Test Building",
                code,
                "Antwerp",
                managerId,
                10,
                "+320000000"
        );
    }
}