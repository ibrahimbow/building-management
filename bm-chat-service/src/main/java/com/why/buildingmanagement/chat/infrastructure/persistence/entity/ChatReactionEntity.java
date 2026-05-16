package com.why.buildingmanagement.chat.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "chat_reactions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_chat_reactions_message_user_emoji",
                        columnNames = {
                                "message_id",
                                "user_id",
                                "emoji"
                        })
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatReactionEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private ChatMessageEntity message;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String emoji;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}