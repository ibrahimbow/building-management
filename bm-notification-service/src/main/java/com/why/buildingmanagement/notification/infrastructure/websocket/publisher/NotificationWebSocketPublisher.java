package com.why.buildingmanagement.notification.infrastructure.websocket.publisher;

import com.why.buildingmanagement.notification.application.result.NotificationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationWebSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publishNotification(final NotificationResult notification) {

        messagingTemplate.convertAndSend(
                        "/topic/users/" + notification.userId() + "/notifications",
                        notification);
    }

    public void publishAnnouncementToBuilding(final NotificationResult notification) {

        messagingTemplate.convertAndSend(
                        "/topic/buildings/" + notification.buildingId() + "/announcements",
                        notification);
    }

}