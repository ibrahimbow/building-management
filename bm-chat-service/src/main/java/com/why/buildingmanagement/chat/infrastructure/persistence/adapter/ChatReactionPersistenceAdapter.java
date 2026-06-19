package com.why.buildingmanagement.chat.infrastructure.persistence.adapter;

import com.why.buildingmanagement.chat.application.port.out.DeleteChatReactionPort;
import com.why.buildingmanagement.chat.application.port.out.LoadChatReactionPort;
import com.why.buildingmanagement.chat.application.port.out.SaveChatReactionPort;
import com.why.buildingmanagement.chat.domain.exception.ChatMessageNotFoundException;
import com.why.buildingmanagement.chat.domain.model.ChatReaction;
import com.why.buildingmanagement.chat.infrastructure.persistence.entity.ChatMessageEntity;
import com.why.buildingmanagement.chat.infrastructure.persistence.entity.ChatReactionEntity;
import com.why.buildingmanagement.chat.infrastructure.persistence.mapper.ChatPersistenceMapper;
import com.why.buildingmanagement.chat.infrastructure.persistence.repository.ChatMessageRepository;
import com.why.buildingmanagement.chat.infrastructure.persistence.repository.ChatReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChatReactionPersistenceAdapter implements SaveChatReactionPort,
                                                       LoadChatReactionPort,
                                                       DeleteChatReactionPort {

    private final ChatReactionRepository chatReactionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatPersistenceMapper chatPersistenceMapper;

    @Override
    public ChatReaction save(final ChatReaction reaction) {

        final ChatMessageEntity messageEntity = chatMessageRepository.findById(reaction.getMessageId())
                                                                     .orElseThrow(() -> new ChatMessageNotFoundException(reaction.getMessageId()));

        final ChatReactionEntity entity = chatPersistenceMapper.toEntity(reaction, messageEntity);

        final ChatReactionEntity savedEntity = chatReactionRepository.save(entity);

        return chatPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByMessageIdAndUserIdAndEmoji(final UUID messageId,
                                                      final Long userId,
                                                      final String emoji) {

        return chatReactionRepository.existsByMessageIdAndUserIdAndEmoji(messageId,
                                                                         userId,
                                                                         emoji);
    }

    @Override
    public void deleteByMessageIdAndUserIdAndEmoji(final UUID messageId,
                                                   final Long userId,
                                                   final String emoji) {

        chatReactionRepository.deleteByMessageIdAndUserIdAndEmoji(messageId,
                                                                  userId,
                                                                  emoji);
    }

    @Override
    public List<ChatReaction> findByMessageIdIn(final List<UUID> messageIds) {

        return chatReactionRepository.findByMessageIdIn(messageIds)
                                     .stream()
                                     .map(chatPersistenceMapper::toDomain)
                                     .toList();
    }
}