package com.why.buildingmanagement.chat.infrastructure.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "chat_messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatMessageEntity {

    @Id
    private UUID id;

    @Column(name = "building_id", nullable = false)
    private UUID buildingId;

    @Column(name = "sender_user_id", nullable = false)
    private Long senderUserId;

    @Column(name = "sender_display_name", nullable = false, length = 150)
    private String senderDisplayName;

    @Column(name = "sender_avatar_url", length = 500)
    private String senderAvatarUrl;

    @Column(name = "content", length = 2000)
    private String content;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @OneToMany(
            mappedBy = "message",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<ChatReactionEntity> reactions = new ArrayList<>();
}