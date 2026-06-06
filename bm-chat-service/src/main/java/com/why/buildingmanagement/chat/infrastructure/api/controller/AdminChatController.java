package com.why.buildingmanagement.chat.infrastructure.api.controller;

import com.why.buildingmanagement.chat.application.port.in.AdminDeleteChatMessageUseCase;
import com.why.buildingmanagement.chat.application.port.in.AdminGetChatMessagesUseCase;
import com.why.buildingmanagement.chat.infrastructure.api.dto.response.ChatMessageResponse;
import com.why.buildingmanagement.chat.infrastructure.api.mapper.ChatApiMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/chat/messages")
@RequiredArgsConstructor
public class AdminChatController {

    private final AdminGetChatMessagesUseCase adminGetChatMessagesUseCase;
    private final AdminDeleteChatMessageUseCase adminDeleteChatMessageUseCase;
    private final ChatApiMapper chatApiMapper;

    @GetMapping
    public ResponseEntity<List<ChatMessageResponse>> getAllMessages() {

        final var response = adminGetChatMessagesUseCase
                        .getAllMessages()
                        .stream()
                        .map(chatApiMapper::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("messageId") final UUID messageId) {

        adminDeleteChatMessageUseCase.deleteMessageByAdmin(messageId);

        return ResponseEntity.noContent().build();
    }
}