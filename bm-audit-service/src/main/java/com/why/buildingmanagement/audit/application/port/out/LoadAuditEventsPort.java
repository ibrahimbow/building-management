package com.why.buildingmanagement.audit.application.port.out;

import com.why.buildingmanagement.audit.domain.model.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoadAuditEventsPort {

    Page<AuditEvent> findAllOrderByCreatedAtDesc(Pageable pageable);
}