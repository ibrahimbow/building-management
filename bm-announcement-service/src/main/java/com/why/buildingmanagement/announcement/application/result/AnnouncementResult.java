package com.why.buildingmanagement.announcement.application.result;

import com.why.buildingmanagement.announcement.domain.model.AnnouncementCategory;

import java.time.Instant;
import java.util.UUID;

public record AnnouncementResult(
        UUID id,
        UUID buildingId,
        Long createdByManagerId,
        String createdBy,
        String title,
        String message,
        AnnouncementCategory category,
        String icon,
        String imageUrl,
        Instant createdAt,
        Instant updatedAt) {
}