package com.why.buildingmanagement.audit.application.port.in;

import com.why.buildingmanagement.audit.domain.model.AuditEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RecordAuditEventCommand(

                Long userId,

                @Size(max = 100)
                String username,

                @NotNull
                AuditEventType eventType,

                @NotBlank
                @Size(max = 1000)
                String description) {
}