package com.why.buildingmanagement.announcement.domain.model;

import com.why.buildingmanagement.announcement.domain.validation.AnnouncementValidator;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Announcement {

    private UUID id;
    private UUID buildingId;
    private Long createdByManagerId;
    private String createdBy;
    private String title;
    private String message;
    private AnnouncementCategory category;
    private String icon;
    private String imageUrl;
    private Instant createdAt;
    private Instant updatedAt;

    private Announcement(final UUID id,
                         final UUID buildingId,
                         final Long createdByManagerId,
                         final String createdBy,
                         final String title,
                         final String message,
                         final AnnouncementCategory category,
                         final String icon,
                         final String imageUrl,
                         final Instant createdAt,
                         final Instant updatedAt) {
        this.id = id;
        this.buildingId = buildingId;
        this.createdByManagerId = createdByManagerId;
        this.createdBy = createdBy;
        this.title = title;
        this.message = message;
        this.category = category;
        this.icon = icon;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Announcement createNew(final UUID buildingId,
                                         final Long managerId,
                                         final String createdBy,
                                         final String title,
                                         final String message,
                                         final AnnouncementCategory category,
                                         final String imageUrl) {

        AnnouncementValidator.validateBuildingId(buildingId);
        AnnouncementValidator.validateManagerId(managerId);
        AnnouncementValidator.validateCreatedBy(createdBy);
        AnnouncementValidator.validateTitle(title);
        AnnouncementValidator.validateMessage(message);
        AnnouncementValidator.validateCategory(category);
        AnnouncementValidator.validateImageUrl(imageUrl);

        final Instant now = Instant.now();

        return new Announcement(null,
                                buildingId,
                                managerId,
                                createdBy,
                                title,
                                message,
                                category,
                                resolveIcon(category),
                                imageUrl,
                                now,
                                null);
    }

    public static Announcement restore(final UUID id,
                                       final UUID buildingId,
                                       final Long createdByManagerId,
                                       final String createdBy,
                                       final String title,
                                       final String message,
                                       final AnnouncementCategory category,
                                       final String icon,
                                       final String imageUrl,
                                       final Instant createdAt,
                                       final Instant updatedAt) {
        return new Announcement(id,
                                buildingId,
                                createdByManagerId,
                                createdBy,
                                title,
                                message,
                                category,
                                icon,
                                imageUrl,
                                createdAt,
                                updatedAt);
    }

    public void update(final String title,
                       final String message,
                       final AnnouncementCategory category,
                       final String imageUrl) {

        AnnouncementValidator.validateTitle(title);
        AnnouncementValidator.validateMessage(message);
        AnnouncementValidator.validateCategory(category);
        AnnouncementValidator.validateImageUrl(imageUrl);

        this.title = title;
        this.message = message;
        this.category = category;
        this.icon = resolveIcon(category);
        this.imageUrl = imageUrl;
        this.updatedAt = Instant.now();
    }

    public boolean belongsToBuilding(final UUID buildingId) {
        return Objects.equals(this.buildingId, buildingId);
    }

    public boolean createdByManager(final Long managerId) {
        return Objects.equals(this.createdByManagerId, managerId);
    }


    private static String resolveIcon(final AnnouncementCategory category) {
        if (category == null) {
            return "info";
        }

        return switch (category) {
            case MAINTENANCE -> "build";
            case EMERGENCY -> "warning";
            case EVENT -> "event";
            case REMINDER -> "reminder";
            case SAFETY -> "safety";
            case GENERAL -> "info";
        };
    }
}