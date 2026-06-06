package com.why.buildingmanagement.shareandhelp.infrastructure.persistence;

import com.why.buildingmanagement.shareandhelp.infrastructure.persistence.entity.ShareAndHelpPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShareAndHelpRepository extends JpaRepository<ShareAndHelpPostEntity, UUID> {

    List<ShareAndHelpPostEntity> findAllByBuildingIdAndDeletedAtIsNullOrderByCreatedAtDesc(final UUID buildingId);

    Optional<ShareAndHelpPostEntity> findByIdAndDeletedAtIsNull(final UUID id);

    Optional<ShareAndHelpPostEntity> findByIdAndCreatedByUserIdAndDeletedAtIsNull(final UUID id,
                                                                                  final Long createdByUserId);

    List<ShareAndHelpPostEntity> findAllByDeletedAtIsNullOrderByCreatedAtDesc();
}