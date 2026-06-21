package com.why.buildingmanagement.audit.infrastructure.kafka.consumer;

import com.why.buildingmanagement.audit.application.port.in.RecordAuditEventCommand;
import com.why.buildingmanagement.audit.application.port.in.RecordAuditEventUseCase;
import com.why.buildingmanagement.audit.infrastructure.kafka.event.AuditEventMessage;
import com.why.buildingmanagement.audit.infrastructure.kafka.topic.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventConsumer {

    private final RecordAuditEventUseCase recordAuditEventUseCase;

    @KafkaListener(
                    topics = KafkaTopics.AUDIT_EVENTS,
                    containerFactory = "auditEventKafkaListenerContainerFactory")
    public void consume(final AuditEventMessage event) {

        log.info(
                        "Received audit event. Type: {}, UserId: {}",
                        event.eventType(),
                        event.userId());

        recordAuditEventUseCase.recordAuditEvent(new RecordAuditEventCommand(event.userId(),
                                                                             event.username(),
                                                                             event.eventType(),
                                                                             event.description()));
    }
}