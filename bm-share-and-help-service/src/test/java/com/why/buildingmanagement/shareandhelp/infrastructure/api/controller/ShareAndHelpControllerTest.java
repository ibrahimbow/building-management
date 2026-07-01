package com.why.buildingmanagement.shareandhelp.infrastructure.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.why.buildingmanagement.shareandhelp.application.port.in.*;
import com.why.buildingmanagement.shareandhelp.application.port.out.LoadManagerBuildingPort;
import com.why.buildingmanagement.shareandhelp.application.port.out.LoadTenantBuildingPort;
import com.why.buildingmanagement.shareandhelp.application.result.ShareAndHelpPostResult;
import com.why.buildingmanagement.shareandhelp.domain.model.ShareAndHelpPostStatus;
import com.why.buildingmanagement.shareandhelp.infrastructure.api.dto.request.AddShareAndHelpCommentRequest;
import com.why.buildingmanagement.shareandhelp.infrastructure.api.dto.request.CreateShareAndHelpPostRequest;
import com.why.buildingmanagement.shareandhelp.infrastructure.api.dto.response.ShareAndHelpPostResponse;
import com.why.buildingmanagement.shareandhelp.infrastructure.api.mapper.ShareAndHelpApiMapper;
import com.why.buildingmanagement.shareandhelp.infrastructure.security.CurrentUser;
import com.why.buildingmanagement.shareandhelp.infrastructure.security.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShareAndHelpController.class)
class ShareAndHelpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateShareAndHelpPostUseCase createShareAndHelpPostUseCase;

    @MockitoBean
    private GetShareAndHelpPostsUseCase getShareAndHelpPostsUseCase;

    @MockitoBean
    private UpdateShareAndHelpPostUseCase updateShareAndHelpPostUseCase;

    @MockitoBean
    private DeleteShareAndHelpPostUseCase deleteShareAndHelpPostUseCase;

    @MockitoBean
    private AddCommentUseCase addCommentUseCase;

    @MockitoBean
    private DeleteCommentUseCase deleteCommentUseCase;

    @MockitoBean
    private ResolveShareAndHelpPostUseCase resolveShareAndHelpPostUseCase;

    @MockitoBean
    private CurrentUserService currentUserService;

    @MockitoBean
    private ShareAndHelpApiMapper shareAndHelpApiMapper;

    @MockitoBean
    private LoadTenantBuildingPort loadTenantBuildingPort;

    @MockitoBean
    private LoadManagerBuildingPort loadManagerBuildingPort;

    @Test
    @WithMockUser(roles = "TENANT")
    void shouldCreateShareAndHelpPost() throws Exception {

        final UUID buildingId = UUID.randomUUID();
        final UUID postId = UUID.randomUUID();

        final CurrentUser currentUser = new CurrentUser(
                        1001L,
                        "tenant@test.com",
                        "TENANT",
                        "Tenant One",
                        null);

        final CreateShareAndHelpPostRequest request = new CreateShareAndHelpPostRequest(
                        "Need a ladder",
                        "Does anyone have a ladder I can borrow this weekend?",
                        null);

        final ShareAndHelpPostResult result = new ShareAndHelpPostResult(
                        postId,
                        buildingId,
                        "Need a ladder",
                        "Does anyone have a ladder I can borrow this weekend?",
                        1001L,
                        "Tenant One",
                        null,
                        Instant.parse("2026-05-14T10:00:00Z"),
                        Instant.parse("2026-05-14T10:00:00Z"),
                        null,
                        ShareAndHelpPostStatus.OPEN,
                        List.of());

        final ShareAndHelpPostResponse response = new ShareAndHelpPostResponse(
                        postId,
                        1001L,
                        "Tenant One",
                        null,
                        "Need a ladder",
                        "Does anyone have a ladder I can borrow this weekend?",
                        null,
                        ShareAndHelpPostStatus.OPEN,
                        Instant.parse("2026-05-14T10:00:00Z"),
                        Instant.parse("2026-05-14T10:00:00Z"),

                        List.of());

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);

        when(loadTenantBuildingPort.loadActiveBuildingIdByTenantUserId(1001L))
                        .thenReturn(buildingId);

        when(createShareAndHelpPostUseCase.create(any(CreateShareAndHelpPostCommand.class)))
                        .thenReturn(result);
        when(shareAndHelpApiMapper.toResponse(result)).thenReturn(response);


        mockMvc.perform(post("/api/tenant/share-and-help/posts")
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated());

        final ArgumentCaptor<CreateShareAndHelpPostCommand> commandCaptor =
                        ArgumentCaptor.forClass(CreateShareAndHelpPostCommand.class);

        verify(createShareAndHelpPostUseCase).create(commandCaptor.capture());

        final CreateShareAndHelpPostCommand command = commandCaptor.getValue();

        assertThat(command.buildingId()).isEqualTo(buildingId);
        assertThat(command.createdByUserId()).isEqualTo(1001L);
        assertThat(command.createdByDisplayName()).isEqualTo("Tenant One");
        assertThat(command.createdByAvatarUrl()).isNull();
        assertThat(command.title()).isEqualTo("Need a ladder");
        assertThat(command.description()).isEqualTo("Does anyone have a ladder I can borrow this weekend?");
        assertThat(command.imageUrl()).isNull();
    }


    @Test
    @WithMockUser(roles = "TENANT")
    void shouldAddComment() throws Exception {
        final UUID buildingId = UUID.randomUUID();
        final UUID postId = UUID.randomUUID();

        final CurrentUser currentUser = new CurrentUser(
                        1001L,
                        "tenant@test.com",
                        "TENANT",
                        "Tenant One",
                        null);

        final AddShareAndHelpCommentRequest request =
                        new AddShareAndHelpCommentRequest(
                                        "I can help you with that.");

        final ShareAndHelpPostResult result = new ShareAndHelpPostResult(
                        postId,
                        buildingId,
                        "Need a ladder",
                        "Does anyone have a ladder I can borrow this weekend?",
                        1001L,
                        "Tenant One",
                        null,
                        Instant.parse("2026-05-14T10:00:00Z"),
                        Instant.parse("2026-05-14T10:00:00Z"),
                        null,
                        ShareAndHelpPostStatus.OPEN,
                        List.of());

        final ShareAndHelpPostResponse response = new ShareAndHelpPostResponse(
                        postId,
                        1001L,
                        "Tenant One",
                        null,
                        "Need a ladder",
                        "Does anyone have a ladder I can borrow this weekend?",
                        null,
                        ShareAndHelpPostStatus.OPEN,
                        Instant.parse("2026-05-14T10:00:00Z"),
                        Instant.parse("2026-05-14T10:00:00Z"),
                        List.of());

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(addCommentUseCase.addComment(any(AddCommentCommand.class)))
                        .thenReturn(result);
        when(shareAndHelpApiMapper.toResponse(result))
                        .thenReturn(response);

        mockMvc.perform(post("/api/tenant/share-and-help/posts/{postId}/comments", postId)
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated());

        final ArgumentCaptor<AddCommentCommand> commandCaptor =
                        ArgumentCaptor.forClass(AddCommentCommand.class);

        verify(addCommentUseCase).addComment(commandCaptor.capture());

        final AddCommentCommand command = commandCaptor.getValue();

        assertThat(command.postId()).isEqualTo(postId);
        assertThat(command.createdByUserId()).isEqualTo(1001L);
        assertThat(command.createdByDisplayName()).isEqualTo("Tenant One");
        assertThat(command.comment()).isEqualTo("I can help you with that.");
    }


    @Test
    @WithMockUser(roles = "TENANT")
    void shouldDeleteComment() throws Exception {

        final UUID postId = UUID.randomUUID();
        final UUID commentId = UUID.randomUUID();

        final CurrentUser currentUser = new CurrentUser(
                        1001L,
                        "tenant@test.com",
                        "TENANT",
                        "Tenant One",
                        null);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);

        mockMvc.perform(delete(
                               "/api/tenant/share-and-help/posts/{postId}/comments/{commentId}",
                               postId,
                               commentId)
                                        .with(csrf()))
               .andExpect(status().isNoContent());

        final ArgumentCaptor<DeleteCommentCommand> commandCaptor =
                        ArgumentCaptor.forClass(DeleteCommentCommand.class);

        verify(deleteCommentUseCase).deleteComment(commandCaptor.capture());

        final DeleteCommentCommand command = commandCaptor.getValue();

        assertThat(command.postId()).isEqualTo(postId);
        assertThat(command.commentId()).isEqualTo(commentId);
        assertThat(command.userId()).isEqualTo(1001L);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void shouldCreateShareAndHelpPostForManager() throws Exception {

        final UUID buildingId = UUID.randomUUID();
        final UUID postId = UUID.randomUUID();

        final CurrentUser currentUser = new CurrentUser(
                        2001L,
                        "manager@test.com",
                        "MANAGER",
                        "Manager One",
                        null);

        final CreateShareAndHelpPostRequest request =
                        new CreateShareAndHelpPostRequest(
                                        "Building update",
                                        "I can help residents with this topic.",
                                        null);

        final ShareAndHelpPostResult result = new ShareAndHelpPostResult(
                        postId,
                        buildingId,
                        "Building update",
                        "I can help residents with this topic.",
                        2001L,
                        "Manager One",
                        null,
                        Instant.parse("2026-05-14T10:00:00Z"),
                        Instant.parse("2026-05-14T10:00:00Z"),
                        null,
                        ShareAndHelpPostStatus.OPEN,
                        List.of());

        final ShareAndHelpPostResponse response = new ShareAndHelpPostResponse(
                        postId,
                        2001L,
                        "Manager One",
                        null,
                        "Building update",
                        "I can help residents with this topic.",
                        null,
                        ShareAndHelpPostStatus.OPEN,
                        Instant.parse("2026-05-14T10:00:00Z"),
                        Instant.parse("2026-05-14T10:00:00Z"),
                        List.of());

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);

        when(loadManagerBuildingPort.loadManagedBuildingIdByManagerUserId(2001L))
                        .thenReturn(buildingId);

        when(createShareAndHelpPostUseCase.create(any(CreateShareAndHelpPostCommand.class)))
                        .thenReturn(result);

        when(shareAndHelpApiMapper.toResponse(result))
                        .thenReturn(response);

        mockMvc.perform(post("/api/tenant/share-and-help/posts")
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated());

        final ArgumentCaptor<CreateShareAndHelpPostCommand> commandCaptor =
                        ArgumentCaptor.forClass(CreateShareAndHelpPostCommand.class);

        verify(createShareAndHelpPostUseCase).create(commandCaptor.capture());

        final CreateShareAndHelpPostCommand command = commandCaptor.getValue();

        assertThat(command.buildingId()).isEqualTo(buildingId);
        assertThat(command.createdByUserId()).isEqualTo(2001L);
        assertThat(command.createdByDisplayName()).isEqualTo("Manager One");
        assertThat(command.title()).isEqualTo("Building update");
        assertThat(command.description()).isEqualTo("I can help residents with this topic.");
    }

    @Test
    @WithMockUser(roles = "TENANT")
    void shouldResolvePost() throws Exception {

        final UUID buildingId = UUID.randomUUID();
        final UUID postId = UUID.randomUUID();

        final CurrentUser currentUser = new CurrentUser(
                        1001L,
                        "tenant@test.com",
                        "TENANT",
                        "Tenant One",
                        null);

        final ShareAndHelpPostResult result =
                        new ShareAndHelpPostResult(
                                        postId,
                                        buildingId,
                                        "Need a ladder",
                                        "Does anyone have a ladder I can borrow this weekend?",
                                        1001L,
                                        "Tenant One",
                                        null,
                                        Instant.parse("2026-05-14T10:00:00Z"),
                                        Instant.parse("2026-05-14T10:00:00Z"),
                                        null,
                                        ShareAndHelpPostStatus.RESOLVED,
                                        List.of());

        final ShareAndHelpPostResponse response =
                        new ShareAndHelpPostResponse(
                                        postId,
                                        1001L,
                                        "Tenant One",
                                        null,
                                        "Need a ladder",
                                        "Does anyone have a ladder I can borrow this weekend?",
                                        null,
                                        ShareAndHelpPostStatus.RESOLVED,
                                        Instant.parse("2026-05-14T10:00:00Z"),
                                        Instant.parse("2026-05-14T10:00:00Z"),
                                        List.of());

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);

        when(loadTenantBuildingPort.loadActiveBuildingIdByTenantUserId(1001L))
                        .thenReturn(buildingId);

        when(resolveShareAndHelpPostUseCase.resolvePost(any(ResolveShareAndHelpPostCommand.class)))
                        .thenReturn(result);

        when(shareAndHelpApiMapper.toResponse(result))
                        .thenReturn(response);

        mockMvc.perform(patch("/api/tenant/share-and-help/posts/{postId}/resolve", postId)
                                        .with(csrf()))
               .andExpect(status().isOk());

        final ArgumentCaptor<ResolveShareAndHelpPostCommand> commandCaptor =
                        ArgumentCaptor.forClass(ResolveShareAndHelpPostCommand.class);

        verify(resolveShareAndHelpPostUseCase).resolvePost(commandCaptor.capture());

        final ResolveShareAndHelpPostCommand command = commandCaptor.getValue();

        assertThat(command.postId()).isEqualTo(postId);
        assertThat(command.buildingId()).isEqualTo(buildingId);
        assertThat(command.currentUserId()).isEqualTo(1001L);
    }


}