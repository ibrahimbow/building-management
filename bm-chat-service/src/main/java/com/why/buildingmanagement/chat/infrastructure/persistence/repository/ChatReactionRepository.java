package com.why.buildingmanagement.chat.infrastructure.persistence.repository;

import com.why.buildingmanagement.chat.infrastructure.persistence.entity.ChatReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ChatReactionRepository extends JpaRepository<ChatReactionEntity, UUID> {

    boolean existsByMessageIdAndUserIdAndEmoji(final UUID messageId,
                                               final Long userId,
                                               final String emoji);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(""" 
                        DELETE FROM ChatReactionEntity reaction
                        WHERE reaction.message.id = :messageId
                        AND reaction.userId = :userId
                        AND reaction.emoji = :emoji
                    """)
    void deleteByMessageIdAndUserIdAndEmoji(
                    @Param("messageId") UUID messageId,
                    @Param("userId") Long userId,
                    @Param("emoji") String emoji);

    List<ChatReactionEntity> findByMessageIdIn(final List<UUID> messageIds);
}