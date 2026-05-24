package com.why.buildingmanagement.notification.application.service;

import com.why.buildingmanagement.notification.application.port.in.*;
import com.why.buildingmanagement.notification.application.port.out.NotificationRepositoryPort;
import com.why.buildingmanagement.notification.application.result.NotificationResult;
import com.why.buildingmanagement.notification.domain.exception.NotificationNotFoundException;
import com.why.buildingmanagement.notification.domain.model.Notification;
import com.why.buildingmanagement.notification.infrastructure.websocket.publisher.NotificationWebSocketPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationApplicationService implements
                CreateNotificationUseCase,
                GetMyNotificationsUseCase,
                GetUnreadNotificationCountUseCase,
                MarkNotificationAsReadUseCase {

    private final NotificationRepositoryPort notificationRepositoryPort;
    private final NotificationWebSocketPublisher notificationWebSocketPublisher;

    @Override
    @Transactional
    public NotificationResult createNotification(final CreateNotificationCommand command) {

        final Notification notification = Notification.createNew(
                        command.userId(),
                        command.buildingId(),
                        command.type(),
                        command.title(),
                        command.message());

        final NotificationResult result = toResult(notificationRepositoryPort.save(notification));

        notificationWebSocketPublisher.publishNotification(result);
        notificationWebSocketPublisher.publishAnnouncementToBuilding(result);

        return result;
    }

    @Override
    public List<NotificationResult> getMyNotifications(final GetMyNotificationsCommand command) {

        return notificationRepositoryPort.findByUserIdAndBuildingIdOrderByCreatedAtDesc(
                                        command.userId(),
                                        command.buildingId())
                        .stream()
                        .map(this::toResult)
                        .toList();
    }

    @Override
    public long getUnreadNotificationCount(final GetUnreadNotificationCountCommand command) {

        return notificationRepositoryPort.countByUserIdAndReadFalse(command.userId());
    }

    @Override
    @Transactional
    public NotificationResult markNotificationAsRead(final MarkNotificationAsReadCommand command) {

        final Notification notification = notificationRepositoryPort
                        .findById(command.notificationId()).orElseThrow(() ->
                                        new NotificationNotFoundException(
                                                        command.notificationId()));

        notification.markAsRead();

        return toResult(notificationRepositoryPort.save(notification));
    }

    private NotificationResult toResult(final Notification notification) {

        return new NotificationResult(
                        notification.getId(),
                        notification.getUserId(),
                        notification.getBuildingId(),
                        notification.getType(),
                        notification.getTitle(),
                        notification.getMessage(),
                        notification.isRead(),
                        notification.getCreatedAt(),
                        notification.getReadAt());
    }
}