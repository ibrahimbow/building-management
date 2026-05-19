package com.why.buildingmanagement.chat.infrastructure.api.controller;

import com.why.buildingmanagement.chat.application.port.in.DeleteChatMessageUseCase;
import com.why.buildingmanagement.chat.application.port.in.GetBuildingChatUseCase;
import com.why.buildingmanagement.chat.application.port.in.ReactToChatMessageCommand;
import com.why.buildingmanagement.chat.application.port.in.ReactToChatMessageUseCase;
import com.why.buildingmanagement.chat.application.port.in.SendChatMessageCommand;
import com.why.buildingmanagement.chat.application.port.in.SendChatMessageUseCase;
import com.why.buildingmanagement.chat.application.result.ChatReactionResult;
import com.why.buildingmanagement.chat.infrastructure.api.dto.request.ReactToChatMessageRequest;
import com.why.buildingmanagement.chat.infrastructure.api.dto.request.SendChatMessageRequest;
import com.why.buildingmanagement.chat.infrastructure.api.dto.response.ChatMessageResponse;
import com.why.buildingmanagement.chat.infrastructure.api.dto.response.ChatReactionResponse;
import com.why.buildingmanagement.chat.infrastructure.api.mapper.ChatApiMapper;
import com.why.buildingmanagement.chat.infrastructure.security.CurrentUser;
import com.why.buildingmanagement.chat.infrastructure.security.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tenant/chat")
public class TenantChatController {

    private final SendChatMessageUseCase sendChatMessageUseCase;
    private final GetBuildingChatUseCase getBuildingChatUseCase;
    private final DeleteChatMessageUseCase deleteChatMessageUseCase;
    private final ReactToChatMessageUseCase reactToChatMessageUseCase;

    private final CurrentUserService currentUserService;

    private final ChatApiMapper chatApiMapper;

    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages() {

        final CurrentUser currentUser = currentUserService.getCurrentUser();

        final List<ChatMessageResponse> response = getBuildingChatUseCase
                .getMessagesForCurrentTenantBuilding(currentUser.userId())
                .stream()
                .map(chatApiMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @Valid @RequestBody final SendChatMessageRequest request) {

        final CurrentUser currentUser = currentUserService.getCurrentUser();

        final SendChatMessageCommand command = new SendChatMessageCommand(
                currentUser.userId(),
                currentUser.displayName(),
                currentUser.avatarUrl(),
                request.content(),
                request.imageUrl());

        final ChatMessageResponse response = chatApiMapper.toResponse(
                sendChatMessageUseCase.send(command));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("messageId") final UUID messageId) {

        final CurrentUser currentUser = currentUserService.getCurrentUser();

        deleteChatMessageUseCase.delete(
                messageId,
                currentUser.userId());

        return ResponseEntity.noContent()
                .build();
    }

    @PostMapping("/messages/{messageId}/reactions")
    public ResponseEntity<ChatReactionResponse> reactToMessage(
            @PathVariable("messageId") final UUID messageId,
            @Valid @RequestBody final ReactToChatMessageRequest request) {

        final CurrentUser currentUser = currentUserService.getCurrentUser();

        final ReactToChatMessageCommand command = new ReactToChatMessageCommand(
                messageId,
                currentUser.userId(),
                request.emoji());

        final ChatReactionResult result = reactToChatMessageUseCase.react(command);

        if (result == null) {
            return ResponseEntity.noContent()
                    .build();
        }

        return ResponseEntity.ok(chatApiMapper.toResponse(result));
    }

    @DeleteMapping("/messages/{messageId}/reactions")
    public ResponseEntity<Void> removeReaction(
            @PathVariable("messageId") final UUID messageId,
            @Valid @RequestBody final ReactToChatMessageRequest request) {

        final CurrentUser currentUser = currentUserService.getCurrentUser();

        reactToChatMessageUseCase.removeReaction(
                new ReactToChatMessageCommand(
                        messageId,
                        currentUser.userId(),
                        request.emoji()));

        return ResponseEntity.noContent()
                .build();
    }
}