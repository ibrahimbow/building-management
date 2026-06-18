package com.why.buildingmanagement.announcement.infrastructure.api.controller.tenant;

import com.why.buildingmanagement.announcement.application.port.in.GetTenantAnnouncementsQuery;
import com.why.buildingmanagement.announcement.application.port.in.GetTenantAnnouncementsUseCase;
import com.why.buildingmanagement.announcement.infrastructure.api.dto.response.AnnouncementResponse;
import com.why.buildingmanagement.announcement.infrastructure.api.mapper.AnnouncementApiMapper;
import com.why.buildingmanagement.announcement.infrastructure.security.CurrentUser;
import com.why.buildingmanagement.announcement.infrastructure.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenant/announcements")
@RequiredArgsConstructor
public class TenantAnnouncementController {

    private final GetTenantAnnouncementsUseCase getTenantAnnouncementsUseCase;
    private final AnnouncementApiMapper mapper;
    private final CurrentUserService currentUserService;

    @GetMapping
    public ResponseEntity<List<AnnouncementResponse>> getTenantAnnouncements() {

        final CurrentUser currentUser = currentUserService.getCurrentUser();

        final List<AnnouncementResponse> announcements = getTenantAnnouncementsUseCase
                        .getTenantAnnouncements(new GetTenantAnnouncementsQuery(currentUser.userId()))
                        .stream()
                        .map(mapper::toResponse)
                        .toList();

        return ResponseEntity.ok(announcements);
    }
}