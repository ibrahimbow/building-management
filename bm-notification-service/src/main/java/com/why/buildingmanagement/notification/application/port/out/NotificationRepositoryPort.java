package com.why.buildingmanagement.notification.application.port.out;

import com.why.buildingmanagement.notification.domain.model.Notification;

public interface NotificationRepositoryPort {

    Notification save(Notification notification);
}