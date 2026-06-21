package com.why.buildingmanagement.audit.infrastructure.persistence;

import com.why.buildingmanagement.audit.AuditServiceApplication;
import com.why.buildingmanagement.audit.domain.model.AuditEvent;
import com.why.buildingmanagement.audit.domain.model.AuditEventType;
import com.why.buildingmanagement.audit.infrastructure.persistence.repository.AuditEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = AuditServiceApplication.class)
@Testcontainers
@ActiveProfiles("test")
class AuditEventPersistenceAdapterTest {

    @Container
    static final PostgreSQLContainer postgres =
                    new PostgreSQLContainer("postgres:16")
                                    .withDatabaseName("building_test_db")
                                    .withUsername("test")
                                    .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private AuditEventPersistenceAdapter adapter;

    @Autowired
    private AuditEventRepository auditEventRepository;

    @BeforeEach
    void cleanDatabase() {

        auditEventRepository.deleteAll();
    }

    @Test
    void save_shouldPersistAuditEvent() {

        final AuditEvent auditEvent = AuditEvent.createNew(15L,
                                                           "ibrahim",
                                                           AuditEventType.USER_LOGIN_SUCCESS,
                                                           "User logged in successfully");

        adapter.save(auditEvent);

        final Page<AuditEvent> auditEvents =
                        adapter.findAllOrderByCreatedAtDesc(PageRequest.of(0, 20));

        assertEquals(1, auditEvents.getTotalElements());

        final AuditEvent savedAuditEvent = auditEvents.getContent().getFirst();

        assertNotNull(savedAuditEvent.getId());
        assertEquals(15L, savedAuditEvent.getUserId());
        assertEquals("ibrahim", savedAuditEvent.getUsername());
        assertEquals(AuditEventType.USER_LOGIN_SUCCESS, savedAuditEvent.getEventType());
        assertEquals("User logged in successfully", savedAuditEvent.getDescription());
        assertNotNull(savedAuditEvent.getCreatedAt());
    }

    @Test
    void findAllOrderByCreatedAtDesc_shouldReturnNewestAuditEventFirst() throws InterruptedException {

        final AuditEvent firstAuditEvent = AuditEvent.createNew(15L,
                                                                "ibrahim",
                                                                AuditEventType.USER_REGISTERED,
                                                                "User registered");

        adapter.save(firstAuditEvent);

        Thread.sleep(10);

        final AuditEvent secondAuditEvent = AuditEvent.createNew(15L,
                                                                 "ibrahim",
                                                                 AuditEventType.USER_LOGIN_SUCCESS,
                                                                 "User logged in successfully");

        adapter.save(secondAuditEvent);

        final Page<AuditEvent> auditEvents =
                        adapter.findAllOrderByCreatedAtDesc(PageRequest.of(0, 20));

        assertEquals(2, auditEvents.getTotalElements());
        assertEquals(AuditEventType.USER_LOGIN_SUCCESS, auditEvents.getContent().getFirst().getEventType());
        assertEquals(AuditEventType.USER_REGISTERED, auditEvents.getContent().getLast().getEventType());
    }
}