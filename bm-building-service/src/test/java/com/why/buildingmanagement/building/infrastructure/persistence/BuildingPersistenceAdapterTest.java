package com.why.buildingmanagement.building.infrastructure.persistence;

import com.why.buildingmanagement.building.application.port.out.BuildingRepositoryPort;
import com.why.buildingmanagement.building.domain.model.Building;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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
    void findByManagerId_shouldReturnBuildingForManager() {
        final Long managerId = 99L;

        buildingRepositoryPort.save(createBuilding("BM-777777", managerId));
        buildingRepositoryPort.save(createBuilding("BM-555555", 100L));

        final Optional<Building> building =
                buildingRepositoryPort.findByManagerId(managerId);

        assertThat(building).isPresent();
        assertThat(building.get().getManagerId()).isEqualTo(managerId);
        assertThat(building.get().getCode()).isEqualTo("BM-777777");
    }


    @Test
    void findByManagerId_shouldReturnEmpty_whenManagerHasNoBuilding() {
        final Optional<Building> building =
                buildingRepositoryPort.findByManagerId(404L);

        assertThat(building).isEmpty();
    }


    @Test
    void save_shouldFail_whenManagerAlreadyHasBuilding() {
        final Long managerId = 99L;

        buildingRepositoryPort.save(createBuilding("BM-111111", managerId));

        final Building secondBuilding = createBuilding("BM-222222", managerId);

        assertThatThrownBy(() -> buildingRepositoryPort.save(secondBuilding))
                .isInstanceOf(DataIntegrityViolationException.class);
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