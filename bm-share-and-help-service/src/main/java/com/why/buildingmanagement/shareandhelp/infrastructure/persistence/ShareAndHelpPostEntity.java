package com.why.buildingmanagement.shareandhelp.infrastructure.persistence;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "share_and_help_posts",
        indexes = {
                @Index(name = "idx_share_help_posts_building_id", columnList = "building_id"),
                @Index(name = "idx_share_help_posts_created_at", columnList = "created_at"),
                @Index(name = "idx_share_help_posts_deleted_at", columnList = "deleted_at")
        })
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ShareAndHelpPostEntity {

    @Id
    private UUID id;

    @Column(name = "building_id", nullable = false)
    private UUID buildingId;

    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    @Column(name = "created_by_display_name", nullable = false, length = 150)
    private String createdByDisplayName;

    @Column(name = "created_by_avatar_url", length = 500)
    private String createdByAvatarUrl;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @OrderBy("createdAt ASC")
    @OneToMany(mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<ShareAndHelpCommentEntity> comments = new ArrayList<>();

    public void replaceComments(final List<ShareAndHelpCommentEntity> comments) {
        this.comments.clear();
        this.comments.addAll(comments);
    }
}