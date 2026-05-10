package com.why.buildingmanagement.announcement.infrastructure.api.controller.manager;

import com.why.buildingmanagement.announcement.application.port.in.CreateAnnouncementCommand;
import com.why.buildingmanagement.announcement.application.port.in.CreateAnnouncementUseCase;
import com.why.buildingmanagement.announcement.application.port.in.DeleteAnnouncementCommand;
import com.why.buildingmanagement.announcement.application.port.in.DeleteAnnouncementUseCase;
import com.why.buildingmanagement.announcement.application.port.in.GetManagerAnnouncementsQuery;
import com.why.buildingmanagement.announcement.application.port.in.GetManagerAnnouncementsUseCase;
import com.why.buildingmanagement.announcement.application.port.in.UpdateAnnouncementCommand;
import com.why.buildingmanagement.announcement.application.port.in.UpdateAnnouncementUseCase;
import com.why.buildingmanagement.announcement.application.result.AnnouncementResult;
import com.why.buildingmanagement.announcement.infrastructure.api.dto.request.CreateAnnouncementRequest;
import com.why.buildingmanagement.announcement.infrastructure.api.dto.request.UpdateAnnouncementRequest;
import com.why.buildingmanagement.announcement.infrastructure.api.dto.response.AnnouncementResponse;
import com.why.buildingmanagement.announcement.infrastructure.api.mapper.AnnouncementApiMapper;
import com.why.buildingmanagement.announcement.infrastructure.security.CurrentUser;
import com.why.buildingmanagement.announcement.infrastructure.security.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/manager/announcements")
@RequiredArgsConstructor
public class ManagerAnnouncementController {

    private final CreateAnnouncementUseCase createAnnouncementUseCase;
    private final UpdateAnnouncementUseCase updateAnnouncementUseCase;
    private final DeleteAnnouncementUseCase deleteAnnouncementUseCase;
    private final GetManagerAnnouncementsUseCase getManagerAnnouncementsUseCase;
    private final AnnouncementApiMapper mapper;
    private final CurrentUserService currentUserService;

    @PostMapping
    public ResponseEntity<AnnouncementResponse> createAnnouncement(
            @Valid @RequestBody final CreateAnnouncementRequest request) {

        System.out.println("🔥 CREATE ANNOUNCEMENT HIT");


        final CurrentUser currentUser = currentUserService.getCurrentUser();


        System.out.println("🔥 USER = " + currentUser);

        try {
            final AnnouncementResult result = createAnnouncementUseCase.createAnnouncement(
                    new CreateAnnouncementCommand(
                            currentUser.userId(),
                            currentUser.username(),
                            request.title(),
                            request.message(),
                            request.category(),
                            request.imageUrl()));

            System.out.println("🔥 RESULT = " + result);

            return ResponseEntity
                    .created(URI.create("/api/manager/announcements/" + result.id()))
                    .body(mapper.toResponse(result));

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<AnnouncementResponse>> getManagerAnnouncements() {

        final CurrentUser currentUser = currentUserService.getCurrentUser();

        final List<AnnouncementResponse> announcements = getManagerAnnouncementsUseCase
                .getManagerAnnouncements(
                        new GetManagerAnnouncementsQuery(currentUser.userId()))
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(announcements);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnnouncementResponse> updateAnnouncement(@PathVariable("id") final UUID announcementId,
                                                                   @Valid @RequestBody final UpdateAnnouncementRequest request) {

        final CurrentUser currentUser = currentUserService.getCurrentUser();

        final AnnouncementResult result = updateAnnouncementUseCase.updateAnnouncement(
                new UpdateAnnouncementCommand(
                        announcementId,
                        currentUser.userId(),
                        request.title(),
                        request.message(),
                        request.category(),
                        request.imageUrl()));

        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable("id") final UUID announcementId) {

        final CurrentUser currentUser = currentUserService.getCurrentUser();

        deleteAnnouncementUseCase.deleteAnnouncement(
                new DeleteAnnouncementCommand(
                        announcementId,
                        currentUser.userId()));

        return ResponseEntity.noContent().build();
    }
}