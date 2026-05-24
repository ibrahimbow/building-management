package com.why.buildingmanagement.notification.infrastructure.persistence.adapter;

import com.why.buildingmanagement.notification.application.port.out.NotificationRepositoryPort;
import com.why.buildingmanagement.notification.domain.model.Notification;
import com.why.buildingmanagement.notification.infrastructure.persistence.mapper.NotificationPersistenceMapper;
import com.why.buildingmanagement.notification.infrastructure.persistence.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationPersistenceAdapter implements NotificationRepositoryPort {

    private final NotificationRepository notificationRepository;
    private final NotificationPersistenceMapper notificationPersistenceMapper;

    @Override
    public Notification save(final Notification notification) {

        return notificationPersistenceMapper.toDomain(
                        notificationRepository.save(notificationPersistenceMapper.toEntity(notification)));
    }

    @Override
    public List<Notification> findByUserIdAndBuildingIdOrderByCreatedAtDesc(final Long userId, final UUID buildingId) {

        return notificationRepository
                        .findByUserIdAndBuildingIdOrderByCreatedAtDesc(
                                        userId,
                                        buildingId)
                        .stream()
                        .map(notificationPersistenceMapper::toDomain)
                        .toList();
    }

    @Override
    public long countByUserIdAndReadFalse(final Long userId) {

        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    public Optional<Notification> findById(final UUID notificationId) {

        return notificationRepository.findById(notificationId).map(notificationPersistenceMapper::toDomain);
    }
}