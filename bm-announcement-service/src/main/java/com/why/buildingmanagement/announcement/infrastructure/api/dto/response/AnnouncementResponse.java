package com.why.buildingmanagement.announcement.infrastructure.api.dto.response;

import com.why.buildingmanagement.announcement.domain.model.AnnouncementCategory;

import java.time.Instant;

public record AnnouncementResponse(
        String id,
        String buildingId,
        String title,
        String message,
        AnnouncementCategory category,
        String icon,
        String imageUrl,
        String createdBy,
        Instant createdAt,
        Instant updatedAt) {
}