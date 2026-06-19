package com.why.buildingmanagement.chat.infrastructure.persistence.entity;

import jakarta.persistence.*;
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
                indexes = {
                                @Index(
                                                name = "idx_chat_reactions_message_id",
                                                columnList = "message_id")
                },
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
    @JoinColumn(
                    name = "message_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "fk_chat_reactions_message"))
    private ChatMessageEntity message;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 20)
    private String emoji;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
