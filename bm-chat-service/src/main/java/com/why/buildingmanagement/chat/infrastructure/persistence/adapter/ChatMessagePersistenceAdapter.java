package com.why.buildingmanagement.chat.infrastructure.persistence.adapter;

import com.why.buildingmanagement.chat.application.port.out.LoadChatMessagePort;
import com.why.buildingmanagement.chat.application.port.out.SaveChatMessagePort;
import com.why.buildingmanagement.chat.domain.model.ChatMessage;
import com.why.buildingmanagement.chat.infrastructure.persistence.entity.ChatMessageEntity;
import com.why.buildingmanagement.chat.infrastructure.persistence.mapper.ChatPersistenceMapper;
import com.why.buildingmanagement.chat.infrastructure.persistence.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChatMessagePersistenceAdapter implements SaveChatMessagePort, LoadChatMessagePort {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatPersistenceMapper chatPersistenceMapper;

    @Override
    public ChatMessage save(final ChatMessage message) {

        final ChatMessageEntity entity = chatPersistenceMapper.toEntity(message);

        final ChatMessageEntity savedEntity = chatMessageRepository.save(entity);

        return chatPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<ChatMessage> findById(final UUID messageId) {

        return chatMessageRepository.findById(messageId)
                        .map(chatPersistenceMapper::toDomain);
    }

    @Override
    public List<ChatMessage> findByBuildingIdOrderByCreatedAtAsc(final UUID buildingId) {

        return chatMessageRepository.findByBuildingIdOrderByCreatedAtAsc(buildingId)
                        .stream()
                        .map(chatPersistenceMapper::toDomain)
                        .toList();
    }

    @Override
    public List<ChatMessage> findAllOrderByCreatedAtDesc() {

        return chatMessageRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc()
                        .stream()
                        .map(chatPersistenceMapper::toDomain)
                        .toList();
    }
}