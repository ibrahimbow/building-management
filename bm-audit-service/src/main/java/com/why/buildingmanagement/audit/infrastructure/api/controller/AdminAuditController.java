package com.why.buildingmanagement.audit.infrastructure.api.controller;

import com.why.buildingmanagement.audit.application.port.in.GetAuditEventsUseCase;
import com.why.buildingmanagement.audit.application.result.AuditEventResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/audit")
public class AdminAuditController {

    private final GetAuditEventsUseCase getAuditEventsUseCase;

    @GetMapping("/events")
    public ResponseEntity<Page<AuditEventResult>> getAuditEvents(
                    @RequestParam(defaultValue = "0") final int page,
                    @RequestParam(defaultValue = "20") final int size) {

        return ResponseEntity.ok(getAuditEventsUseCase.getAuditEvents(page, size));
    }
}