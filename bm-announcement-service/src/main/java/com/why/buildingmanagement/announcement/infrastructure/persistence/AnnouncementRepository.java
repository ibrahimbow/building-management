package com.why.buildingmanagement.announcement.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnnouncementRepository extends JpaRepository<AnnouncementEntity, UUID> {

    Page<AnnouncementEntity> findByBuildingIdOrderByCreatedAtDesc(UUID buildingId, Pageable pageable);

    Optional<AnnouncementEntity> findByIdAndCreatedByManagerId(UUID id, Long createdByManagerId);

    List<AnnouncementEntity> findAllByOrderByCreatedAtDesc();

}