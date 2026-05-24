package com.why.buildingmanagement.notification.application.port.in;

import com.why.buildingmanagement.notification.application.result.NotificationResult;
import jakarta.validation.Valid;

import java.util.List;

public interface GetMyNotificationsUseCase {

    List<NotificationResult> getMyNotifications(@Valid GetMyNotificationsCommand command);
}