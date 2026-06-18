package com.why.buildingmanagement.announcement.application.service;

import com.why.buildingmanagement.announcement.application.port.in.*;
import com.why.buildingmanagement.announcement.application.port.out.AnnouncementRepositoryPort;
import com.why.buildingmanagement.announcement.application.port.out.BuildingAccessPort;
import com.why.buildingmanagement.announcement.application.result.AnnouncementResult;
import com.why.buildingmanagement.announcement.domain.exception.AnnouncementNotFoundException;
import com.why.buildingmanagement.announcement.domain.exception.AnnouncementOwnershipException;
import com.why.buildingmanagement.announcement.domain.model.Announcement;
import com.why.buildingmanagement.announcement.infrastructure.kafka.event.AnnouncementCreatedEvent;
import com.why.buildingmanagement.announcement.infrastructure.kafka.publisher.AnnouncementEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementApplicationService implements CreateAnnouncementUseCase,
                                                       UpdateAnnouncementUseCase,
                                                       DeleteAnnouncementUseCase,
                                                       GetManagerAnnouncementsUseCase,
                                                       GetTenantAnnouncementsUseCase,
                                                       GetManagerAnnouncementByIdUseCase,
                                                       AdminGetAnnouncementsUseCase,
                                                       AdminDeleteAnnouncementUseCase {

    private final AnnouncementRepositoryPort announcementRepositoryPort;
    private final BuildingAccessPort buildingAccessPort;
    private final AnnouncementEventPublisher announcementEventPublisher;

    @Override
    @Transactional
    public AnnouncementResult createAnnouncement(final CreateAnnouncementCommand command) {
        Objects.requireNonNull(command, "CreateAnnouncementCommand must not be null");

        final UUID buildingId = buildingAccessPort.getManagerBuildingId(command.managerId());

        final Announcement announcement = Announcement.createNew(buildingId,
                                                                 command.managerId(),
                                                                 command.createdBy(),
                                                                 command.title(),
                                                                 command.message(),
                                                                 command.category(),
                                                                 command.imageUrl());

        final Announcement savedAnnouncement = announcementRepositoryPort.save(announcement);

        publishAnnouncementCreated(savedAnnouncement, command.createdBy());

        return toResult(savedAnnouncement);
    }

    @Override
    @Transactional
    public AnnouncementResult updateAnnouncement(final UpdateAnnouncementCommand command) {
        Objects.requireNonNull(command, "UpdateAnnouncementCommand must not be null");

        final Announcement announcement = getManagerOwnedAnnouncement(command.announcementId(), command.managerId());

        announcement.update(command.title(),
                            command.message(),
                            command.category(),
                            command.imageUrl());

        return toResult(announcementRepositoryPort.save(announcement));
    }

    @Override
    @Transactional
    public void deleteAnnouncement(final DeleteAnnouncementCommand command) {
        Objects.requireNonNull(command, "DeleteAnnouncementCommand must not be null");

        final Announcement announcement = getManagerOwnedAnnouncement(command.announcementId(), command.managerId());

        announcementRepositoryPort.delete(announcement);
    }

    @Override
    public List<AnnouncementResult> getManagerAnnouncements(final GetManagerAnnouncementsQuery query) {
        Objects.requireNonNull(query, "GetManagerAnnouncementsQuery must not be null");

        final UUID buildingId = buildingAccessPort.getManagerBuildingId(query.managerId());

        return announcementRepositoryPort.findByBuildingId(buildingId)
                                         .stream()
                                         .map(this::toResult)
                                         .toList();
    }

    @Override
    public List<AnnouncementResult> getTenantAnnouncements(final GetTenantAnnouncementsQuery query) {
        Objects.requireNonNull(query, "GetTenantAnnouncementsQuery must not be null");

        final UUID buildingId = buildingAccessPort.getTenantActiveBuildingId(query.tenantUserId());

        return announcementRepositoryPort.findByBuildingId(buildingId)
                                         .stream()
                                         .map(this::toResult)
                                         .toList();
    }

    @Override
    public AnnouncementResult getManagerAnnouncementById(final GetManagerAnnouncementByIdQuery query) {
        Objects.requireNonNull(query, "GetManagerAnnouncementByIdQuery must not be null");

        final Announcement announcement = getManagerOwnedAnnouncement(query.announcementId(), query.managerId());

        return toResult(announcement);
    }

    @Override
    public List<AnnouncementResult> getAllAnnouncements() {
        return announcementRepositoryPort.findAll()
                                         .stream()
                                         .map(this::toResult)
                                         .toList();
    }

    @Override
    @Transactional
    public void deleteAnnouncementByAdmin(final UUID announcementId) {
        Objects.requireNonNull(announcementId, "announcementId must not be null");

        final Announcement announcement = getAnnouncementOrThrow(announcementId);

        announcementRepositoryPort.delete(announcement);
    }

    private Announcement getAnnouncementOrThrow(final UUID announcementId) {
        Objects.requireNonNull(announcementId, "announcementId must not be null");

        return announcementRepositoryPort.findById(announcementId)
                                         .orElseThrow(() -> new AnnouncementNotFoundException(announcementId));
    }

    private Announcement getManagerOwnedAnnouncement(final UUID announcementId, final Long managerId) {
        Objects.requireNonNull(managerId, "managerId must not be null");

        final Announcement announcement = getAnnouncementOrThrow(announcementId);

        final UUID managerBuildingId = buildingAccessPort.getManagerBuildingId(managerId);

        if (!announcement.belongsToBuilding(managerBuildingId) || !announcement.createdByManager(managerId)) {
            throw new AnnouncementOwnershipException(announcementId);
        }

        return announcement;
    }

    private void publishAnnouncementCreated(final Announcement announcement, final String createdBy) {
        Objects.requireNonNull(announcement, "announcement must not be null");

        announcementEventPublisher.publishAnnouncementCreated(
                        new AnnouncementCreatedEvent(
                                        announcement.getId(),
                                        announcement.getBuildingId(),
                                        announcement.getTitle(),
                                        announcement.getCategory().name(),
                                        createdBy,
                                        announcement.getCreatedAt()));
    }

    private AnnouncementResult toResult(final Announcement announcement) {
        return new AnnouncementResult(announcement.getId(),
                                      announcement.getBuildingId(),
                                      announcement.getCreatedByManagerId(),
                                      announcement.getCreatedBy(),
                                      announcement.getTitle(),
                                      announcement.getMessage(),
                                      announcement.getCategory(),
                                      announcement.getIcon(),
                                      announcement.getImageUrl(),
                                      announcement.getCreatedAt(),
                                      announcement.getUpdatedAt());
    }
}