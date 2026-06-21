package com.why.buildingmanagement.audit.application.port.in;

public interface RecordAuditEventUseCase {

    void recordAuditEvent(final RecordAuditEventCommand command);
}
