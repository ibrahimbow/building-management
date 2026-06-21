package com.why.buildingmanagement.audit.application.port.out;

import com.why.buildingmanagement.audit.domain.model.AuditEvent;

public interface SaveAuditEventPort {

    void save(final AuditEvent auditEvent);
}
