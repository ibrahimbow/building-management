package com.why.buildingmanagement.notification.infrastructure.persistence.mapper;

import com.why.buildingmanagement.notification.domain.model.Notification;
import com.why.buildingmanagement.notification.infrastructure.persistence.entity.NotificationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationPersistenceMapper {

    default NotificationEntity toEntity(final Notification notification) {

        return new NotificationEntity(
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

    default Notification toDomain(final NotificationEntity entity) {

        return Notification.restore(
                        entity.getId(),
                        entity.getUserId(),
                        entity.getBuildingId(),
                        entity.getType(),
                        entity.getTitle(),
                        entity.getMessage(),
                        entity.isRead(),
                        entity.getCreatedAt(),
                        entity.getReadAt());
    }
}