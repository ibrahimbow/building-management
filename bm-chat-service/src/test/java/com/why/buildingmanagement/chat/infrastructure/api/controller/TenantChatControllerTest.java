package com.why.buildingmanagement.chat.infrastructure.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.why.buildingmanagement.chat.application.port.in.*;
import com.why.buildingmanagement.chat.application.result.ChatMessageResult;
import com.why.buildingmanagement.chat.application.result.ChatReactionResult;
import com.why.buildingmanagement.chat.infrastructure.api.mapper.ChatApiMapperImpl;
import com.why.buildingmanagement.chat.infrastructure.security.CurrentUser;
import com.why.buildingmanagement.chat.infrastructure.security.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TenantChatController.class)
@Import(ChatApiMapperImpl.class)
class TenantChatControllerTest {

    private static final UUID MESSAGE_ID = UUID.randomUUID();
    private static final UUID REACTION_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final Long TENANT_USER_ID = 1001L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SendChatMessageUseCase sendChatMessageUseCase;

    @MockitoBean
    private GetBuildingChatUseCase getBuildingChatUseCase;

    @MockitoBean
    private ReactToChatMessageUseCase reactToChatMessageUseCase;

    @MockitoBean
    private DeleteChatMessageUseCase deleteChatMessageUseCase;

    @MockitoBean
    private CurrentUserService currentUserService;

    @BeforeEach
    void setUp() {

        when(currentUserService.getCurrentUser())
                .thenReturn(new CurrentUser(
                        TENANT_USER_ID,
                        "tenant@test.com",
                        "TENANT",
                        "Tenant User",
                        ""));
    }

    @Test
    void shouldSendMessage() throws Exception {

        when(sendChatMessageUseCase.send(any(SendChatMessageCommand.class)))
                .thenReturn(chatMessageResult(false));

        final String requestBody = objectMapper.writeValueAsString(Map.of(
                "content", "Hello chat",
                "imageUrl", ""
        ));

        mockMvc.perform(post("/api/tenant/chat/messages")
                        .with(user("tenant").roles("TENANT"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(MESSAGE_ID.toString())))
                .andExpect(jsonPath("$.senderUserId", is(TENANT_USER_ID.intValue())))
                .andExpect(jsonPath("$.senderDisplayName", is("Tenant User")))
                .andExpect(jsonPath("$.content", is("Hello chat")))
                .andExpect(jsonPath("$.deleted", is(false)));

        final ArgumentCaptor<SendChatMessageCommand> commandCaptor =
                ArgumentCaptor.forClass(SendChatMessageCommand.class);

        verify(sendChatMessageUseCase).send(commandCaptor.capture());

        assertThat(commandCaptor.getValue().senderUserId()).isEqualTo(TENANT_USER_ID);
        assertThat(commandCaptor.getValue().senderDisplayName()).isEqualTo("Tenant User");
        assertThat(commandCaptor.getValue().senderAvatarUrl()).isEqualTo("");
        assertThat(commandCaptor.getValue().content()).isEqualTo("Hello chat");
        assertThat(commandCaptor.getValue().imageUrl()).isEqualTo("");
    }

    @Test
    void shouldGetBuildingChat() throws Exception {

        when(getBuildingChatUseCase.getMessagesForCurrentTenantBuilding(TENANT_USER_ID))
                .thenReturn(List.of(chatMessageResult(false)));

        mockMvc.perform(get("/api/tenant/chat/messages")
                        .with(user("tenant").roles("TENANT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(MESSAGE_ID.toString())))
                .andExpect(jsonPath("$[0].senderUserId", is(TENANT_USER_ID.intValue())))
                .andExpect(jsonPath("$[0].senderDisplayName", is("Tenant User")))
                .andExpect(jsonPath("$[0].content", is("Hello chat")))
                .andExpect(jsonPath("$[0].deleted", is(false)));
    }

    @Test
    void shouldReactToMessage() throws Exception {

        when(reactToChatMessageUseCase.react(any(ReactToChatMessageCommand.class)))
                .thenReturn(chatReactionResult());

        final String requestBody = objectMapper.writeValueAsString(Map.of(
                "emoji", "👍"
        ));

        mockMvc.perform(post("/api/tenant/chat/messages/{messageId}/reactions", MESSAGE_ID)
                        .with(user("tenant").roles("TENANT"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        final ArgumentCaptor<ReactToChatMessageCommand> commandCaptor =
                ArgumentCaptor.forClass(ReactToChatMessageCommand.class);

        verify(reactToChatMessageUseCase).react(commandCaptor.capture());

        assertThat(commandCaptor.getValue().messageId()).isEqualTo(MESSAGE_ID);
        assertThat(commandCaptor.getValue().userId()).isEqualTo(TENANT_USER_ID);
        assertThat(commandCaptor.getValue().emoji()).isEqualTo("👍");
    }

    @Test
    void shouldSoftDeleteMessage() throws Exception {

        mockMvc.perform(delete("/api/tenant/chat/messages/{messageId}", MESSAGE_ID)
                        .with(user("tenant").roles("TENANT"))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(deleteChatMessageUseCase).delete(MESSAGE_ID, TENANT_USER_ID);
    }

    private ChatMessageResult chatMessageResult(final boolean deleted) {

        return new ChatMessageResult(
                MESSAGE_ID,
                BUILDING_ID,
                TENANT_USER_ID,
                "Tenant User",
                "",
                "Hello chat",
                "",
                deleted,
                Instant.now(),
                Instant.now(),
                deleted ? Instant.now() : null,
                List.of());
    }

    private ChatReactionResult chatReactionResult() {

        return new ChatReactionResult(
                REACTION_ID,
                MESSAGE_ID,
                TENANT_USER_ID,
                "👍",
                Instant.now());
    }
}