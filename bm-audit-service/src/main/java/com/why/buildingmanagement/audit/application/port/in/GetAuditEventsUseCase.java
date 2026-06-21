package com.why.buildingmanagement.audit.application.port.in;

import com.why.buildingmanagement.audit.application.result.AuditEventResult;
import org.springframework.data.domain.Page;

public interface GetAuditEventsUseCase {

    Page<AuditEventResult> getAuditEvents(int page, int size);
}