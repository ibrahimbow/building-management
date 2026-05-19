package com.why.buildingmanagement.chat.infrastructure.persistence.adapter;

import com.why.buildingmanagement.chat.domain.model.ChatMessage;
import com.why.buildingmanagement.chat.infrastructure.persistence.entity.ChatMessageEntity;
import com.why.buildingmanagement.chat.infrastructure.persistence.mapper.ChatPersistenceMapper;
import com.why.buildingmanagement.chat.infrastructure.persistence.repository.ChatMessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatMessagePersistenceAdapterTest {

    @Mock
    private ChatMessageRepository repository;

    @Mock
    private ChatPersistenceMapper mapper;

    @InjectMocks
    private ChatMessagePersistenceAdapter adapter;

    @Test
    void shouldSaveChatMessage() {

        final ChatMessage message = createMessage();
        final ChatMessageEntity entity = createEntity(message);
        final ChatMessageEntity savedEntity = createEntity(message);
        final ChatMessage savedMessage = createMessage();

        when(mapper.toEntity(message)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedMessage);

        final ChatMessage result = adapter.save(message);

        assertThat(result).isEqualTo(savedMessage);

        verify(mapper).toEntity(message);
        verify(repository).save(entity);
        verify(mapper).toDomain(savedEntity);
    }

    @Test
    void shouldFindChatMessageById() {

        final UUID messageId = UUID.randomUUID();
        final ChatMessage message = createMessage();
        final ChatMessageEntity entity = createEntity(message);

        when(repository.findById(messageId))
                .thenReturn(Optional.of(entity));

        when(mapper.toDomain(entity))
                .thenReturn(message);

        final Optional<ChatMessage> result = adapter.findById(messageId);

        assertThat(result).isPresent();
        assertThat(result).contains(message);

        verify(repository).findById(messageId);
        verify(mapper).toDomain(entity);
    }

    @Test
    void shouldReturnEmptyWhenChatMessageNotFoundById() {

        final UUID messageId = UUID.randomUUID();

        when(repository.findById(messageId))
                .thenReturn(Optional.empty());

        final Optional<ChatMessage> result = adapter.findById(messageId);

        assertThat(result).isEmpty();

        verify(repository).findById(messageId);
    }

    @Test
    void shouldFindMessagesByBuildingIdOrderedByCreatedAtAsc() {

        final UUID buildingId = UUID.randomUUID();
        final ChatMessage message = createMessage();
        final ChatMessageEntity entity = createEntity(message);

        when(repository.findByBuildingIdOrderByCreatedAtAsc(buildingId))
                .thenReturn(List.of(entity));

        when(mapper.toDomain(entity))
                .thenReturn(message);

        final List<ChatMessage> result =
                adapter.findByBuildingIdOrderByCreatedAtAsc(buildingId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(message);

        verify(repository).findByBuildingIdOrderByCreatedAtAsc(buildingId);
        verify(mapper).toDomain(entity);
    }

    private static ChatMessage createMessage() {

        return ChatMessage.restore(
                UUID.randomUUID(),
                UUID.randomUUID(),
                1001L,
                "Tenant User",
                null,
                "Hello chat",
                null,
                Instant.parse("2026-05-19T10:00:00Z"),
                Instant.parse("2026-05-19T10:00:00Z"),
                null,
                List.of());
    }

    private static ChatMessageEntity createEntity(final ChatMessage message) {

        return new ChatMessageEntity(
                message.getId(),
                message.getBuildingId(),
                message.getSenderUserId(),
                message.getSenderDisplayName(),
                message.getSenderAvatarUrl(),
                message.getContent(),
                message.getImageUrl(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getDeletedAt(),
                new java.util.ArrayList<>());
    }
}