package com.why.buildingmanagement.announcement.infrastructure.persistence;

import com.why.buildingmanagement.announcement.domain.model.Announcement;
import com.why.buildingmanagement.announcement.domain.model.AnnouncementCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@Import({
        AnnouncementPersistenceAdapter.class,
        AnnouncementMapperImpl.class
})
class AnnouncementPersistenceAdapterTest {

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
    private AnnouncementPersistenceAdapter adapter;

    @Autowired
    private AnnouncementRepository repository;

    private UUID buildingId;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        buildingId = UUID.randomUUID();
    }

    @Test
    void save_shouldPersistAnnouncement() {
        final Announcement announcement = createAnnouncement();

        final Announcement savedAnnouncement = adapter.save(announcement);

        assertThat(savedAnnouncement.getId()).isNotNull();
        assertThat(savedAnnouncement.getBuildingId()).isEqualTo(buildingId);
        assertThat(savedAnnouncement.getCreatedByManagerId()).isEqualTo(1L);
        assertThat(savedAnnouncement.getCreatedBy()).isEqualTo("Ibrahim");
        assertThat(savedAnnouncement.getTitle()).isEqualTo("Water maintenance");
        assertThat(savedAnnouncement.getMessage()).isEqualTo("Water will be off tomorrow");
        assertThat(savedAnnouncement.getCategory()).isEqualTo(AnnouncementCategory.MAINTENANCE);
        assertThat(savedAnnouncement.getIcon()).isEqualTo("build");
        assertThat(savedAnnouncement.getImageUrl()).isEqualTo("https://example.com/water.jpg");
        assertThat(savedAnnouncement.getCreatedAt()).isNotNull();
        assertThat(savedAnnouncement.getUpdatedAt()).isNull();
    }

    @Test
    void findById_shouldReturnAnnouncement_whenAnnouncementExists() {
        final Announcement savedAnnouncement = adapter.save(createAnnouncement());

        final Optional<Announcement> result = adapter.findById(savedAnnouncement.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedAnnouncement.getId());
        assertThat(result.get().getBuildingId()).isEqualTo(buildingId);
        assertThat(result.get().getTitle()).isEqualTo("Water maintenance");
    }

    @Test
    void findById_shouldReturnEmpty_whenAnnouncementDoesNotExist() {
        final Optional<Announcement> result = adapter.findById(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    void findByBuildingId_shouldReturnOnlyAnnouncementsForBuilding() {
        final Announcement firstAnnouncement = adapter.save(createAnnouncement());

        final UUID otherBuildingId = UUID.randomUUID();

        final Announcement otherAnnouncement = Announcement.createNew(
                otherBuildingId,
                1L,
                "Ibrahim",
                "Other building announcement",
                "Other message",
                AnnouncementCategory.REMINDER,
                null);

        adapter.save(otherAnnouncement);

        final List<Announcement> results = adapter.findByBuildingId(buildingId);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getId()).isEqualTo(firstAnnouncement.getId());
        assertThat(results.getFirst().getBuildingId()).isEqualTo(buildingId);
    }

    @Test
    void delete_shouldRemoveAnnouncement() {
        final Announcement savedAnnouncement = adapter.save(createAnnouncement());

        adapter.delete(savedAnnouncement);

        final Optional<Announcement> result = adapter.findById(savedAnnouncement.getId());

        assertThat(result).isEmpty();
    }

    private Announcement createAnnouncement() {
        return Announcement.createNew(
                buildingId,
                1L,
                "Ibrahim",
                "Water maintenance",
                "Water will be off tomorrow",
                AnnouncementCategory.MAINTENANCE,
                "https://example.com/water.jpg");
    }
}