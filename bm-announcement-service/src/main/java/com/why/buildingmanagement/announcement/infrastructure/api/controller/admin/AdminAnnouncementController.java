package com.why.buildingmanagement.announcement.infrastructure.api.controller.admin;

import com.why.buildingmanagement.announcement.application.port.in.AdminDeleteAnnouncementUseCase;
import com.why.buildingmanagement.announcement.application.port.in.AdminGetAnnouncementsUseCase;
import com.why.buildingmanagement.announcement.infrastructure.api.dto.response.AnnouncementResponse;
import com.why.buildingmanagement.announcement.infrastructure.api.mapper.AnnouncementApiMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/announcements")
@RequiredArgsConstructor
public class AdminAnnouncementController {

    private final AdminGetAnnouncementsUseCase adminGetAnnouncementsUseCase;
    private final AdminDeleteAnnouncementUseCase adminDeleteAnnouncementUseCase;
    private final AnnouncementApiMapper announcementApiMapper;

    @GetMapping
    public ResponseEntity<List<AnnouncementResponse>> getAllAnnouncements() {

        final List<AnnouncementResponse> response = adminGetAnnouncementsUseCase
                        .getAllAnnouncements()
                        .stream()
                        .map(announcementApiMapper::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{announcementId}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable("announcementId") final UUID announcementId) {

        adminDeleteAnnouncementUseCase.deleteAnnouncementByAdmin(announcementId);

        return ResponseEntity.noContent().build();
    }
}