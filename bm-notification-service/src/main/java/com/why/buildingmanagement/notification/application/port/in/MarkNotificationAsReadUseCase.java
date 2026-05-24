package com.why.buildingmanagement.notification.application.port.in;

import com.why.buildingmanagement.notification.application.result.NotificationResult;
import jakarta.validation.Valid;

public interface MarkNotificationAsReadUseCase {

    NotificationResult markNotificationAsRead(@Valid MarkNotificationAsReadCommand command);
}