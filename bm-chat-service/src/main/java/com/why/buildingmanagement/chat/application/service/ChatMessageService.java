package com.why.buildingmanagement.chat.application.service;

import com.why.buildingmanagement.chat.application.port.in.*;
import com.why.buildingmanagement.chat.application.port.out.*;
import com.why.buildingmanagement.chat.application.result.ChatMessageResult;
import com.why.buildingmanagement.chat.application.result.ChatReactionResult;
import com.why.buildingmanagement.chat.application.result.ChatReactionSummaryResult;
import com.why.buildingmanagement.chat.domain.exception.ChatMessageAccessDeniedException;
import com.why.buildingmanagement.chat.domain.exception.ChatMessageNotFoundException;
import com.why.buildingmanagement.chat.domain.model.ChatMessage;
import com.why.buildingmanagement.chat.domain.model.ChatReaction;
import com.why.buildingmanagement.chat.infrastructure.websocket.ChatWebSocketPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatMessageService implements SendChatMessageUseCase,
        GetBuildingChatUseCase,
        DeleteChatMessageUseCase,
        ReactToChatMessageUseCase {

    private final SaveChatMessagePort saveChatMessagePort;
    private final LoadChatMessagePort loadChatMessagePort;
    private final LoadTenantBuildingPort loadTenantBuildingPort;
    private final SaveChatReactionPort saveChatReactionPort;
    private final LoadChatReactionPort loadChatReactionPort;
    private final DeleteChatReactionPort deleteChatReactionPort;
    private final ChatWebSocketPublisher chatWebSocketPublisher;

    @Override
    public ChatMessageResult send(final SendChatMessageCommand command) {

        final UUID buildingId = loadTenantBuildingPort
                .loadActiveBuildingIdByTenantUserId(command.senderUserId());

        final ChatMessage message = ChatMessage.createNew(
                buildingId,
                command.senderUserId(),
                command.senderDisplayName(),
                command.senderAvatarUrl(),
                command.content(),
                command.imageUrl());

        final ChatMessage savedMessage = saveChatMessagePort.save(message);

        final ChatMessageResult result = toMessageResult(
                savedMessage,
                command.senderUserId());

        chatWebSocketPublisher.publishMessageCreated(result);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResult> getMessagesForCurrentTenantBuilding(final Long tenantUserId) {

        final UUID buildingId = loadTenantBuildingPort
                .loadActiveBuildingIdByTenantUserId(tenantUserId);

        final List<ChatMessage> messages = loadChatMessagePort
                .findByBuildingIdOrderByCreatedAtAsc(buildingId);

        final List<UUID> messageIds = messages.stream()
                .map(ChatMessage::getId)
                .toList();

        final List<ChatReaction> reactions = loadChatReactionPort.findByMessageIdIn(messageIds);

        return messages.stream()
                .map(message -> toMessageResult(
                        message,
                        reactions,
                        tenantUserId))
                .toList();
    }

    @Override
    public void delete(final UUID messageId,
                       final Long currentUserId) {

        final ChatMessage message = loadChatMessagePort.findById(messageId)
                .orElseThrow(() -> new ChatMessageNotFoundException(messageId));

        if (!message.isOwnedBy(currentUserId)) {
            throw new ChatMessageAccessDeniedException(messageId, currentUserId);
        }

        message.delete();

        final ChatMessage savedMessage = saveChatMessagePort.save(message);
        final ChatMessageResult result = toMessageResult(
                savedMessage,
                currentUserId);

        chatWebSocketPublisher.publishMessageDeleted(result);
    }

    @Override
    public ChatReactionResult react(final ReactToChatMessageCommand command) {

        final ChatMessage message = loadChatMessagePort.findById(command.messageId())
                .orElseThrow(() -> new ChatMessageNotFoundException(command.messageId()));

        if (message.isDeleted()) {
            throw new ChatMessageNotFoundException(command.messageId());
        }

        final boolean alreadyReacted = loadChatReactionPort.existsByMessageIdAndUserIdAndEmoji(
                command.messageId(),
                command.userId(),
                command.emoji());

        if (alreadyReacted) {

            deleteChatReactionPort.deleteByMessageIdAndUserIdAndEmoji(
                    command.messageId(),
                    command.userId(),
                    command.emoji());

            return null;
        }

        final ChatReaction reaction = ChatReaction.createNew(
                command.messageId(),
                command.userId(),
                command.emoji());

        final ChatReaction savedReaction = saveChatReactionPort.save(reaction);

        publishReactionUpdated(message, command.userId());

        return toReactionResult(savedReaction);
    }

    @Override
    public void removeReaction(final ReactToChatMessageCommand command) {

        deleteChatReactionPort.deleteByMessageIdAndUserIdAndEmoji(
                command.messageId(),
                command.userId(),
                command.emoji());

        final ChatMessage message = loadChatMessagePort.findById(command.messageId())
                .orElseThrow(() -> new ChatMessageNotFoundException(command.messageId()));

        publishReactionUpdated(message, command.userId());
    }

    private ChatMessageResult toMessageResult(final ChatMessage message,
                                              final Long currentUserId) {

        return toMessageResult(
                message,
                message.getReactions(),
                currentUserId);
    }

    private ChatMessageResult toMessageResult(final ChatMessage message,
                                              final List<ChatReaction> reactions,
                                              final Long currentUserId) {

        return new ChatMessageResult(
                message.getId(),
                message.getBuildingId(),
                message.getSenderUserId(),
                message.getSenderDisplayName(),
                message.getSenderAvatarUrl(),
                message.getContent(),
                message.getImageUrl(),
                message.isDeleted(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getDeletedAt(),
                toReactionSummaryResults(
                        message.getId(),
                        reactions,
                        currentUserId));
    }

    private List<ChatReactionSummaryResult> toReactionSummaryResults(final UUID messageId,
                                                                     final List<ChatReaction> reactions,
                                                                     final Long currentUserId) {

        final Map<String, List<ChatReaction>> groupedByEmoji = reactions.stream()
                .filter(reaction -> reaction.getMessageId().equals(messageId))
                .collect(Collectors.groupingBy(ChatReaction::getEmoji));

        return groupedByEmoji.entrySet()
                .stream()
                .map(entry -> new ChatReactionSummaryResult(
                        entry.getKey(),
                        entry.getValue().size(),
                        entry.getValue()
                                .stream()
                                .anyMatch(reaction -> reaction.getUserId().equals(currentUserId))))
                .toList();
    }

    private ChatReactionResult toReactionResult(final ChatReaction reaction) {

        return new ChatReactionResult(
                reaction.getId(),
                reaction.getMessageId(),
                reaction.getUserId(),
                reaction.getEmoji(),
                reaction.getCreatedAt());
    }

    private void publishReactionUpdated(final ChatMessage message, final Long currentUserId) {

        final List<ChatReaction> reactions = loadChatReactionPort.findByMessageIdIn(
                List.of(message.getId()));

        final ChatMessageResult result = toMessageResult(
                message,
                reactions,
                currentUserId);

        chatWebSocketPublisher.publishReactionUpdated(result);
    }
}