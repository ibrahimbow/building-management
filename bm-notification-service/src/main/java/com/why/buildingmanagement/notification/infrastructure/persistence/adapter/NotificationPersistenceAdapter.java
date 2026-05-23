package com.why.buildingmanagement.notification.infrastructure.persistence.adapter;

import com.why.buildingmanagement.notification.application.port.out.NotificationRepositoryPort;
import com.why.buildingmanagement.notification.domain.model.Notification;
import com.why.buildingmanagement.notification.infrastructure.persistence.mapper.NotificationPersistenceMapper;
import com.why.buildingmanagement.notification.infrastructure.persistence.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationPersistenceAdapter implements NotificationRepositoryPort {

    private final NotificationRepository notificationRepository;
    private final NotificationPersistenceMapper notificationPersistenceMapper;

    @Override
    public Notification save(final Notification notification) {
        return notificationPersistenceMapper.toDomain(notificationRepository
                        .save(notificationPersistenceMapper.toEntity(notification)));
    }
}