package com.why.buildingmanagement.shareandhelp.infrastructure.persistence;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "share_and_help_comments",
        indexes = {
                @Index(name = "idx_share_help_comments_post_id", columnList = "post_id"),
                @Index(name = "idx_share_help_comments_created_at", columnList = "created_at"),
                @Index(name = "idx_share_help_comments_deleted_at", columnList = "deleted_at")
        })
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ShareAndHelpCommentEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private ShareAndHelpPostEntity post;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    @Column(name = "created_by_display_name", nullable = false, length = 150)
    private String createdByDisplayName;

    @Column(name = "created_by_avatar_url", length = 500)
    private String createdByAvatarUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}