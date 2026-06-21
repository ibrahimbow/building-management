package com.why.buildingmanagement.audit.application.service;

import com.why.buildingmanagement.audit.application.port.in.GetAuditEventsUseCase;
import com.why.buildingmanagement.audit.application.port.in.RecordAuditEventCommand;
import com.why.buildingmanagement.audit.application.port.in.RecordAuditEventUseCase;
import com.why.buildingmanagement.audit.application.port.out.LoadAuditEventsPort;
import com.why.buildingmanagement.audit.application.port.out.SaveAuditEventPort;
import com.why.buildingmanagement.audit.application.result.AuditEventResult;
import com.why.buildingmanagement.audit.domain.model.AuditEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditEventService implements RecordAuditEventUseCase, GetAuditEventsUseCase {

    private final SaveAuditEventPort saveAuditEventPort;
    private final LoadAuditEventsPort loadAuditEventsPort;

    @Override
    public void recordAuditEvent(final RecordAuditEventCommand command) {

        final AuditEvent auditEvent = AuditEvent.createNew(command.userId(),
                                                           command.username(),
                                                           command.eventType(),
                                                           command.description());

        saveAuditEventPort.save(auditEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditEventResult> getAuditEvents(final int page,
                                                 final int size) {

        return loadAuditEventsPort.findAllOrderByCreatedAtDesc(PageRequest.of(page, size))
                                  .map(this::toAuditEventResult);
    }

    private AuditEventResult toAuditEventResult(final AuditEvent auditEvent) {

        return new AuditEventResult(auditEvent.getId(),
                                    auditEvent.getUserId(),
                                    auditEvent.getUsername(),
                                    auditEvent.getEventType(),
                                    auditEvent.getDescription(),
                                    auditEvent.getCreatedAt());
    }
}