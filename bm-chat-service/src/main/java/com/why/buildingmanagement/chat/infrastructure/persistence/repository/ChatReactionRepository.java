package com.why.buildingmanagement.chat.infrastructure.persistence.repository;

import com.why.buildingmanagement.chat.infrastructure.persistence.entity.ChatReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatReactionRepository extends JpaRepository<ChatReactionEntity, UUID> {

    boolean existsByMessageIdAndUserIdAndEmoji(final UUID messageId,
                                               final Long userId,
                                               final String emoji);

    void deleteByMessageIdAndUserIdAndEmoji(final UUID messageId,
                                            final Long userId,
                                            final String emoji);

    List<ChatReactionEntity> findByMessageIdIn(final List<UUID> messageIds);
}