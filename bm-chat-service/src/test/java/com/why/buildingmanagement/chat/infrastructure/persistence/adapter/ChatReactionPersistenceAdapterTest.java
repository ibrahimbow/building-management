package com.why.buildingmanagement.chat.infrastructure.persistence.adapter;

import com.why.buildingmanagement.chat.domain.model.ChatReaction;
import com.why.buildingmanagement.chat.infrastructure.persistence.entity.ChatMessageEntity;
import com.why.buildingmanagement.chat.infrastructure.persistence.entity.ChatReactionEntity;
import com.why.buildingmanagement.chat.infrastructure.persistence.mapper.ChatPersistenceMapper;
import com.why.buildingmanagement.chat.infrastructure.persistence.repository.ChatReactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatReactionPersistenceAdapterTest {

    @Mock
    private ChatReactionRepository repository;

    @Mock
    private ChatPersistenceMapper mapper;

    @InjectMocks
    private ChatReactionPersistenceAdapter adapter;

    @Test
    void shouldCheckIfReactionExists() {

        final UUID messageId = UUID.randomUUID();
        final Long userId = 1001L;
        final String emoji = "👍";

        when(repository.existsByMessageIdAndUserIdAndEmoji(messageId, userId, emoji))
                .thenReturn(true);

        final boolean result = adapter.existsByMessageIdAndUserIdAndEmoji(
                messageId,
                userId,
                emoji);

        assertThat(result).isTrue();

        verify(repository).existsByMessageIdAndUserIdAndEmoji(
                messageId,
                userId,
                emoji);
    }

    @Test
    void shouldDeleteReactionByMessageIdAndUserIdAndEmoji() {

        final UUID messageId = UUID.randomUUID();
        final Long userId = 1001L;
        final String emoji = "👍";

        adapter.deleteByMessageIdAndUserIdAndEmoji(
                messageId,
                userId,
                emoji);

        verify(repository).deleteByMessageIdAndUserIdAndEmoji(
                messageId,
                userId,
                emoji);
    }

    @Test
    void shouldFindReactionsByMessageIds() {

        final ChatReaction reaction = createReaction();
        final ChatReactionEntity entity = createEntity(reaction);

        when(repository.findByMessageIdIn(List.of(reaction.getMessageId())))
                .thenReturn(List.of(entity));

        when(mapper.toDomain(entity))
                .thenReturn(reaction);

        final List<ChatReaction> result =
                adapter.findByMessageIdIn(List.of(reaction.getMessageId()));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(reaction);

        verify(repository).findByMessageIdIn(List.of(reaction.getMessageId()));
        verify(mapper).toDomain(entity);
    }

    private static ChatReaction createReaction() {

        return ChatReaction.restore(
                UUID.randomUUID(),
                UUID.randomUUID(),
                1001L,
                "👍",
                Instant.parse("2026-05-19T10:00:00Z"));
    }

    private static ChatReactionEntity createEntity(final ChatReaction reaction) {

        return new ChatReactionEntity(
                reaction.getId(),
                new ChatMessageEntity(
                        reaction.getMessageId(),
                        UUID.randomUUID(),
                        1001L,
                        "Tenant User",
                        null,
                        "Hello chat",
                        null,
                        Instant.parse("2026-05-19T10:00:00Z"),
                        Instant.parse("2026-05-19T10:00:00Z"),
                        null,
                        new java.util.ArrayList<>()),
                reaction.getUserId(),
                reaction.getEmoji(),
                reaction.getCreatedAt());
    }
}