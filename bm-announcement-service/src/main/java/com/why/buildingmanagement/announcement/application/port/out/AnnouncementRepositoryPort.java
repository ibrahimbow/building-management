package com.why.buildingmanagement.announcement.application.port.out;

import com.why.buildingmanagement.announcement.domain.model.Announcement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnnouncementRepositoryPort {

    Announcement save(Announcement announcement);

    Optional<Announcement> findById(UUID id);

    List<Announcement> findByBuildingId(UUID buildingId);

    void delete(Announcement announcement);

    Optional<Announcement> findByIdAndManagerId(UUID announcementId, Long managerId);
}