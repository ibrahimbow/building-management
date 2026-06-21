package com.why.buildingmanagement.audit.infrastructure.api.controller;

import com.why.buildingmanagement.audit.application.port.in.GetAuditEventsUseCase;
import com.why.buildingmanagement.audit.application.result.AuditEventResult;
import com.why.buildingmanagement.audit.domain.model.AuditEventType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminAuditController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminAuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetAuditEventsUseCase getAuditEventsUseCase;

    @Test
    void getAuditEvents_shouldReturnAuditEvents() throws Exception {

        final UUID id = UUID.randomUUID();

        final AuditEventResult auditEvent = new AuditEventResult(
                        id,
                        15L,
                        "ibrahim",
                        AuditEventType.USER_LOGIN_SUCCESS,
                        "User logged in successfully",
                        Instant.now());

        when(getAuditEventsUseCase.getAuditEvents(0, 20))
                        .thenReturn(new PageImpl<>(List.of(auditEvent)));

        mockMvc.perform(get("/api/admin/audit/events"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content.length()").value(1))
               .andExpect(jsonPath("$.content[0].id").value(id.toString()))
               .andExpect(jsonPath("$.content[0].userId").value(15))
               .andExpect(jsonPath("$.content[0].username").value("ibrahim"))
               .andExpect(jsonPath("$.content[0].eventType").value("USER_LOGIN_SUCCESS"))
               .andExpect(jsonPath("$.content[0].description").value("User logged in successfully"))
               .andExpect(jsonPath("$.totalElements").value(1));
    }
}