package com.why.buildingmanagement.announcement.infrastructure.persistence;

import com.why.buildingmanagement.announcement.domain.model.AnnouncementCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "announcements",
        indexes = {
                @Index(
                        name = "idx_announcements_building_id_created_at",
                        columnList = "building_id, created_at DESC")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AnnouncementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "building_id", nullable = false)
    private UUID buildingId;

    @Column(name = "created_by_manager_id", nullable = false)
    private Long createdByManagerId;

    @Column(name = "created_by", nullable = false, length = 255)
    private String createdBy;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private AnnouncementCategory category;

    @Column(length = 100)
    private String icon;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}