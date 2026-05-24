package com.why.buildingmanagement.notification.infrastructure.persistence.repository;

import com.why.buildingmanagement.notification.infrastructure.persistence.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    List<NotificationEntity> findByUserIdAndBuildingIdOrderByCreatedAtDesc(Long userId, UUID buildingId);

    long countByUserIdAndReadFalse(Long userId);
}
