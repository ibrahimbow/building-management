package com.why.buildingmanagement.notification.infrastructure.api.controller;

import com.why.buildingmanagement.notification.application.port.in.GetMyNotificationsCommand;
import com.why.buildingmanagement.notification.application.port.in.GetMyNotificationsUseCase;
import com.why.buildingmanagement.notification.application.port.in.GetUnreadNotificationCountCommand;
import com.why.buildingmanagement.notification.application.port.in.GetUnreadNotificationCountUseCase;
import com.why.buildingmanagement.notification.application.port.in.MarkNotificationAsReadCommand;
import com.why.buildingmanagement.notification.application.port.in.MarkNotificationAsReadUseCase;
import com.why.buildingmanagement.notification.application.result.NotificationResult;
import com.why.buildingmanagement.notification.domain.model.NotificationType;
import com.why.buildingmanagement.notification.infrastructure.security.CurrentUser;
import com.why.buildingmanagement.notification.infrastructure.security.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    private static final Long USER_ID = 10L;

    private final GetMyNotificationsUseCase getMyNotificationsUseCase =
                    mock(GetMyNotificationsUseCase.class);

    private final GetUnreadNotificationCountUseCase getUnreadNotificationCountUseCase =
                    mock(GetUnreadNotificationCountUseCase.class);

    private final MarkNotificationAsReadUseCase markNotificationAsReadUseCase =
                    mock(MarkNotificationAsReadUseCase.class);

    private final CurrentUserService currentUserService =
                    mock(CurrentUserService.class);

    private final NotificationController notificationController =
                    new NotificationController(
                                    getMyNotificationsUseCase,
                                    getUnreadNotificationCountUseCase,
                                    markNotificationAsReadUseCase,
                                    currentUserService);

    private final MockMvc mockMvc =
                    MockMvcBuilders.standaloneSetup(notificationController)
                                    .build();

    @Test
    void shouldGetMyNotifications() throws Exception {
        final UUID notificationId = UUID.randomUUID();
        final UUID buildingId = UUID.randomUUID();

        when(currentUserService.getCurrentUser())
                        .thenReturn(currentUser());

        when(getMyNotificationsUseCase.getMyNotifications(any(GetMyNotificationsCommand.class)))
                        .thenReturn(List.of(new NotificationResult(
                                        notificationId,
                                        USER_ID,
                                        buildingId,
                                        NotificationType.ANNOUNCEMENT,
                                        "New announcement",
                                        "Water maintenance",
                                        false,
                                        Instant.parse("2026-05-29T10:00:00Z"),
                                        null)));

        mockMvc.perform(get("/api/notifications"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].id").value(notificationId.toString()))
                        .andExpect(jsonPath("$[0].userId").value(USER_ID))
                        .andExpect(jsonPath("$[0].buildingId").value(buildingId.toString()))
                        .andExpect(jsonPath("$[0].type").value("ANNOUNCEMENT"))
                        .andExpect(jsonPath("$[0].title").value("New announcement"))
                        .andExpect(jsonPath("$[0].message").value("Water maintenance"))
                        .andExpect(jsonPath("$[0].read").value(false));

        final ArgumentCaptor<GetMyNotificationsCommand> captor =
                        ArgumentCaptor.forClass(GetMyNotificationsCommand.class);

        verify(getMyNotificationsUseCase).getMyNotifications(captor.capture());
        assertThat(captor.getValue().userId()).isEqualTo(USER_ID);

        verify(currentUserService).getCurrentUser();
        verifyNoMoreInteractions(
                        currentUserService,
                        getMyNotificationsUseCase,
                        getUnreadNotificationCountUseCase,
                        markNotificationAsReadUseCase);
    }

    @Test
    void shouldGetUnreadNotificationCount() throws Exception {
        when(currentUserService.getCurrentUser())
                        .thenReturn(currentUser());

        when(getUnreadNotificationCountUseCase.getUnreadNotificationCount(
                        any(GetUnreadNotificationCountCommand.class)))
                        .thenReturn(5L);

        mockMvc.perform(get("/api/notifications/unread-count"))
                        .andExpect(status().isOk())
                        .andExpect(content().string("5"));

        final ArgumentCaptor<GetUnreadNotificationCountCommand> captor =
                        ArgumentCaptor.forClass(GetUnreadNotificationCountCommand.class);

        verify(getUnreadNotificationCountUseCase)
                        .getUnreadNotificationCount(captor.capture());

        assertThat(captor.getValue().userId()).isEqualTo(USER_ID);

        verify(currentUserService).getCurrentUser();
        verifyNoMoreInteractions(
                        currentUserService,
                        getMyNotificationsUseCase,
                        getUnreadNotificationCountUseCase,
                        markNotificationAsReadUseCase);
    }

    @Test
    void shouldMarkNotificationAsRead() throws Exception {
        final UUID notificationId = UUID.randomUUID();
        final UUID buildingId = UUID.randomUUID();

        when(currentUserService.getCurrentUser())
                        .thenReturn(currentUser());

        when(markNotificationAsReadUseCase.markNotificationAsRead(
                        any(MarkNotificationAsReadCommand.class)))
                        .thenReturn(new NotificationResult(
                                        notificationId,
                                        USER_ID,
                                        buildingId,
                                        NotificationType.CHAT,
                                        "New chat message",
                                        "Ibrahim sent a new message",
                                        true,
                                        Instant.parse("2026-05-29T10:00:00Z"),
                                        Instant.parse("2026-05-29T10:05:00Z")));

        mockMvc.perform(patch("/api/notifications/{notificationId}/read", notificationId))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(notificationId.toString()))
                        .andExpect(jsonPath("$.userId").value(USER_ID))
                        .andExpect(jsonPath("$.buildingId").value(buildingId.toString()))
                        .andExpect(jsonPath("$.type").value("CHAT"))
                        .andExpect(jsonPath("$.read").value(true))
                        .andExpect(jsonPath("$.readAt").exists());

        final ArgumentCaptor<MarkNotificationAsReadCommand> captor =
                        ArgumentCaptor.forClass(MarkNotificationAsReadCommand.class);

        verify(markNotificationAsReadUseCase)
                        .markNotificationAsRead(captor.capture());

        assertThat(captor.getValue().notificationId()).isEqualTo(notificationId);
        assertThat(captor.getValue().userId()).isEqualTo(USER_ID);

        verify(currentUserService).getCurrentUser();
        verifyNoMoreInteractions(
                        currentUserService,
                        getMyNotificationsUseCase,
                        getUnreadNotificationCountUseCase,
                        markNotificationAsReadUseCase);
    }

    @Test
    void shouldReturnBadRequestWhenNotificationIdIsInvalidUuid() throws Exception {
        mockMvc.perform(patch("/api/notifications/{notificationId}/read", "invalid-id"))
                        .andExpect(status().isBadRequest());

        verifyNoInteractions(
                        currentUserService,
                        getMyNotificationsUseCase,
                        getUnreadNotificationCountUseCase,
                        markNotificationAsReadUseCase);
    }

    private CurrentUser currentUser() {
        return new CurrentUser(
                        USER_ID,
                        "tenant@example.com",
                        "TENANT",
                        "Tenant User",
                        null);
    }
}