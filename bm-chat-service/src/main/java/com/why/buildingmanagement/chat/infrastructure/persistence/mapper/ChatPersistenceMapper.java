package com.why.buildingmanagement.chat.infrastructure.persistence.mapper;

import com.why.buildingmanagement.chat.domain.model.ChatMessage;
import com.why.buildingmanagement.chat.domain.model.ChatReaction;
import com.why.buildingmanagement.chat.infrastructure.persistence.entity.ChatMessageEntity;
import com.why.buildingmanagement.chat.infrastructure.persistence.entity.ChatReactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatPersistenceMapper {

    @Mapping(target = "reactions", ignore = true)
    ChatMessageEntity toEntity(final ChatMessage message);

    default ChatMessage toDomain(final ChatMessageEntity entity) {

        return ChatMessage.restore(
                entity.getId(),
                entity.getBuildingId(),
                entity.getSenderUserId(),
                entity.getSenderDisplayName(),
                entity.getSenderAvatarUrl(),
                entity.getContent(),
                entity.getImageUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt(),
                entity.getReactions()
                        .stream()
                        .map(this::toDomain)
                        .toList());
    }

    @Mapping(target = "message", ignore = true)
    ChatReactionEntity toEntity(final ChatReaction reaction);

    default ChatReaction toDomain(final ChatReactionEntity entity) {

        return ChatReaction.restore(
                entity.getId(),
                entity.getMessage().getId(),
                entity.getUserId(),
                entity.getEmoji(),
                entity.getCreatedAt());
    }
}