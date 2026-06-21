package com.why.buildingmanagement.audit.infrastructure.persistence.mapper;

import com.why.buildingmanagement.audit.domain.model.AuditEvent;
import com.why.buildingmanagement.audit.infrastructure.persistence.entity.AuditEventEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditEventMapper {

    AuditEventEntity toEntity(final AuditEvent auditEvent);

    default AuditEvent toDomain(final AuditEventEntity entity) {
        return AuditEvent.restore(entity.getId(),
                                  entity.getUserId(),
                                  entity.getUsername(),
                                  entity.getEventType(),
                                  entity.getDescription(),
                                  entity.getCreatedAt());
    }
}