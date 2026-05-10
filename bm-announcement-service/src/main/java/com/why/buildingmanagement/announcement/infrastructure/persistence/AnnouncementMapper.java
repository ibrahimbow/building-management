package com.why.buildingmanagement.announcement.infrastructure.persistence;

import com.why.buildingmanagement.announcement.domain.model.Announcement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnnouncementMapper {

    default AnnouncementEntity toEntity(final Announcement announcement) {
        return new AnnouncementEntity(
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

    default Announcement toDomain(final AnnouncementEntity entity) {
        return Announcement.restore(
                entity.getId(),
                entity.getBuildingId(),
                entity.getCreatedByManagerId(),
                entity.getCreatedBy(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getCategory(),
                entity.getIcon(),
                entity.getImageUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}