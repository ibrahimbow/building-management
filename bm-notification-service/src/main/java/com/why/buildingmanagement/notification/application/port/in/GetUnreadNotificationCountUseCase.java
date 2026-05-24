package com.why.buildingmanagement.notification.application.port.in;

import jakarta.validation.Valid;

public interface GetUnreadNotificationCountUseCase {

    long getUnreadNotificationCount(@Valid GetUnreadNotificationCountCommand command);
}