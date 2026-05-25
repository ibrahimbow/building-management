package com.why.buildingmanagement.notification.infrastructure.api.controller;

import com.why.buildingmanagement.notification.application.port.in.GetMyNotificationsCommand;
import com.why.buildingmanagement.notification.application.port.in.GetMyNotificationsUseCase;
import com.why.buildingmanagement.notification.application.port.in.GetUnreadNotificationCountCommand;
import com.why.buildingmanagement.notification.application.port.in.GetUnreadNotificationCountUseCase;
import com.why.buildingmanagement.notification.application.port.in.MarkNotificationAsReadCommand;
import com.why.buildingmanagement.notification.application.port.in.MarkNotificationAsReadUseCase;
import com.why.buildingmanagement.notification.application.result.NotificationResult;
import com.why.buildingmanagement.notification.infrastructure.security.CurrentUser;
import com.why.buildingmanagement.notification.infrastructure.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final GetMyNotificationsUseCase getMyNotificationsUseCase;
    private final GetUnreadNotificationCountUseCase getUnreadNotificationCountUseCase;
    private final MarkNotificationAsReadUseCase markNotificationAsReadUseCase;
    private final CurrentUserService currentUserService;

    @GetMapping
    public List<NotificationResult> getMyNotifications() {

        final CurrentUser currentUser = currentUserService.getCurrentUser();

        return getMyNotificationsUseCase.getMyNotifications(
                        new GetMyNotificationsCommand(currentUser.id()));
    }

    @GetMapping("/unread-count")
    public long getUnreadCount() {

        final CurrentUser currentUser = currentUserService.getCurrentUser();

        return getUnreadNotificationCountUseCase.getUnreadNotificationCount(
                        new GetUnreadNotificationCountCommand(
                                        currentUser.id()));
    }

    @PatchMapping("/{notificationId}/read")
    public NotificationResult markAsRead(@PathVariable("notificationId") final UUID notificationId) {

        final CurrentUser currentUser = currentUserService.getCurrentUser();

        return markNotificationAsReadUseCase.markNotificationAsRead(
                        new MarkNotificationAsReadCommand(
                                        notificationId,
                                        currentUser.id()));
    }
}