package com.why.buildingmanagement.announcement.infrastructure.api.mapper;

import com.why.buildingmanagement.announcement.application.result.AnnouncementResult;
import com.why.buildingmanagement.announcement.infrastructure.api.dto.response.AnnouncementResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnnouncementApiMapper {

    default AnnouncementResponse toResponse(final AnnouncementResult result) {
        return new AnnouncementResponse(
                result.id().toString(),
                result.buildingId().toString(),
                result.title(),
                result.message(),
                result.category(),
                result.icon(),
                result.imageUrl(),
                result.createdBy(),
                result.createdAt(),
                result.updatedAt());
    }
}