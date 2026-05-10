package com.why.buildingmanagement.announcement.application.service;

import com.why.buildingmanagement.announcement.application.port.in.*;
import com.why.buildingmanagement.announcement.application.port.out.AnnouncementRepositoryPort;
import com.why.buildingmanagement.announcement.application.port.out.BuildingAccessPort;
import com.why.buildingmanagement.announcement.application.result.AnnouncementResult;
import com.why.buildingmanagement.announcement.domain.exception.AnnouncementNotFoundException;
import com.why.buildingmanagement.announcement.domain.exception.AnnouncementOwnershipException;
import com.why.buildingmanagement.announcement.domain.model.Announcement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementApplicationService implements
        CreateAnnouncementUseCase,
        UpdateAnnouncementUseCase,
        DeleteAnnouncementUseCase,
        GetManagerAnnouncementsUseCase,
        GetTenantAnnouncementsUseCase {

    private final AnnouncementRepositoryPort announcementRepositoryPort;
    private final BuildingAccessPort buildingAccessPort;

    @Override
    @Transactional
    public AnnouncementResult createAnnouncement(final CreateAnnouncementCommand command) {

        final UUID buildingId = buildingAccessPort.getManagerBuildingId(command.managerId());

        final Announcement announcement = Announcement.createNew(
                buildingId,
                command.managerId(),
                command.createdBy(),
                command.title(),
                command.message(),
                command.category(),
                command.imageUrl());

        final Announcement savedAnnouncement = announcementRepositoryPort.save(announcement);

        return toResult(savedAnnouncement);
    }

    @Override
    @Transactional
    public AnnouncementResult updateAnnouncement(final UpdateAnnouncementCommand command) {

        final Announcement announcement = announcementRepositoryPort
                .findById(command.announcementId())
                .orElseThrow(() -> new AnnouncementNotFoundException(command.announcementId()));

        final UUID managerBuildingId = buildingAccessPort.getManagerBuildingId(command.managerId());

        if (!announcement.belongsToBuilding(managerBuildingId)
                || !announcement.createdByManager(command.managerId())) {
            throw new AnnouncementOwnershipException(command.announcementId());
        }

        announcement.update(
                command.title(),
                command.message(),
                command.category(),
                command.imageUrl());

        final Announcement savedAnnouncement = announcementRepositoryPort.save(announcement);

        return toResult(savedAnnouncement);
    }

    @Override
    @Transactional
    public void deleteAnnouncement(final DeleteAnnouncementCommand command) {

        final Announcement announcement = announcementRepositoryPort
                .findById(command.announcementId())
                .orElseThrow(() -> new AnnouncementNotFoundException(command.announcementId()));

        final UUID managerBuildingId = buildingAccessPort.getManagerBuildingId(command.managerId());

        if (!announcement.belongsToBuilding(managerBuildingId)
                || !announcement.createdByManager(command.managerId())) {
            throw new AnnouncementOwnershipException(command.announcementId());
        }

        announcementRepositoryPort.delete(announcement);
    }

    @Override
    public List<AnnouncementResult> getManagerAnnouncements(final GetManagerAnnouncementsQuery query) {

        final UUID buildingId = buildingAccessPort.getManagerBuildingId(query.managerId());

        return announcementRepositoryPort.findByBuildingId(buildingId)
                .stream()
                .map(this::toResult)
                .toList();
    }

    @Override
    public List<AnnouncementResult> getTenantAnnouncements(final GetTenantAnnouncementsQuery query) {

        final UUID buildingId = buildingAccessPort.getTenantActiveBuildingId(query.tenantUserId());

        return announcementRepositoryPort.findByBuildingId(buildingId)
                .stream()
                .map(this::toResult)
                .toList();
    }

    private AnnouncementResult toResult(final Announcement announcement) {
        return new AnnouncementResult(
                announcement.getId(),
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