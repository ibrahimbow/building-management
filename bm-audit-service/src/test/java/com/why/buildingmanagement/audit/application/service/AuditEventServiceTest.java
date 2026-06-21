package com.why.buildingmanagement.audit.application.service;

import com.why.buildingmanagement.audit.application.port.in.RecordAuditEventCommand;
import com.why.buildingmanagement.audit.application.port.out.LoadAuditEventsPort;
import com.why.buildingmanagement.audit.application.port.out.SaveAuditEventPort;
import com.why.buildingmanagement.audit.application.result.AuditEventResult;
import com.why.buildingmanagement.audit.domain.model.AuditEvent;
import com.why.buildingmanagement.audit.domain.model.AuditEventType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditEventServiceTest {

    @Mock
    private SaveAuditEventPort saveAuditEventPort;

    @Mock
    private LoadAuditEventsPort loadAuditEventsPort;

    @InjectMocks
    private AuditEventService auditEventService;

    @Test
    void recordAuditEvent_shouldCreateAndSaveAuditEvent() {

        final RecordAuditEventCommand command = new RecordAuditEventCommand(15L,
                                                                            "ibrahim",
                                                                            AuditEventType.USER_LOGIN_SUCCESS,
                                                                            "User logged in successfully");

        auditEventService.recordAuditEvent(command);

        final ArgumentCaptor<AuditEvent> auditEventCaptor = ArgumentCaptor.forClass(AuditEvent.class);

        verify(saveAuditEventPort).save(auditEventCaptor.capture());

        final AuditEvent savedAuditEvent = auditEventCaptor.getValue();

        assertNotNull(savedAuditEvent);
        assertEquals(15L, savedAuditEvent.getUserId());
        assertEquals("ibrahim", savedAuditEvent.getUsername());
        assertEquals(AuditEventType.USER_LOGIN_SUCCESS, savedAuditEvent.getEventType());
        assertEquals("User logged in successfully", savedAuditEvent.getDescription());
        assertNotNull(savedAuditEvent.getCreatedAt());
    }

    @Test
    void getAuditEvents_shouldReturnPagedAuditEventResults() {

        final AuditEvent auditEvent = AuditEvent.restore(UUID.randomUUID(),
                                                         15L,
                                                         "ibrahim",
                                                         AuditEventType.USER_LOGIN_SUCCESS,
                                                         "User logged in successfully",
                                                         Instant.now());

        when(loadAuditEventsPort.findAllOrderByCreatedAtDesc(any(PageRequest.class)))
                        .thenReturn(new PageImpl<>(List.of(auditEvent)));

        final Page<AuditEventResult> results = auditEventService.getAuditEvents(0, 20);

        assertEquals(1, results.getTotalElements());
        assertEquals(auditEvent.getId(), results.getContent().getFirst().id());
        assertEquals(15L, results.getContent().getFirst().userId());
        assertEquals("ibrahim", results.getContent().getFirst().username());
        assertEquals(AuditEventType.USER_LOGIN_SUCCESS, results.getContent().getFirst().eventType());
        assertEquals("User logged in successfully", results.getContent().getFirst().description());
        assertEquals(auditEvent.getCreatedAt(), results.getContent().getFirst().createdAt());

        verify(loadAuditEventsPort).findAllOrderByCreatedAtDesc(PageRequest.of(0, 20));
    }
}