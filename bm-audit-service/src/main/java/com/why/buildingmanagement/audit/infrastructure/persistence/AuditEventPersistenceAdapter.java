package com.why.buildingmanagement.audit.infrastructure.persistence;

import com.why.buildingmanagement.audit.application.port.out.LoadAuditEventsPort;
import com.why.buildingmanagement.audit.application.port.out.SaveAuditEventPort;
import com.why.buildingmanagement.audit.domain.model.AuditEvent;
import com.why.buildingmanagement.audit.infrastructure.persistence.mapper.AuditEventMapper;
import com.why.buildingmanagement.audit.infrastructure.persistence.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuditEventPersistenceAdapter implements SaveAuditEventPort, LoadAuditEventsPort {

    private final AuditEventRepository auditEventRepository;
    private final AuditEventMapper auditEventMapper;

    @Override
    public void save(final AuditEvent auditEvent) {

        auditEventMapper.toDomain(auditEventRepository.save(auditEventMapper.toEntity(auditEvent)));
    }

    @Override
    public Page<AuditEvent> findAllOrderByCreatedAtDesc(final Pageable pageable) {

        return auditEventRepository
                        .findAllByOrderByCreatedAtDesc(pageable)
                        .map(auditEventMapper::toDomain);
    }
}