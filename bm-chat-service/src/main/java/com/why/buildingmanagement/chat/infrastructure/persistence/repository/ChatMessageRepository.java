package com.why.buildingmanagement.chat.infrastructure.persistence.repository;

import com.why.buildingmanagement.chat.infrastructure.persistence.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository
        extends JpaRepository<ChatMessageEntity, UUID> {

    List<ChatMessageEntity> findByBuildingIdOrderByCreatedAtAsc(final UUID buildingId);
}