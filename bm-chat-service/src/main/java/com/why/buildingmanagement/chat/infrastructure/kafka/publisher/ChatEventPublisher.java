package com.why.buildingmanagement.chat.infrastructure.kafka.publisher;

import com.why.buildingmanagement.chat.infrastructure.kafka.event.ChatMessageCreatedEvent;
import com.why.buildingmanagement.chat.infrastructure.kafka.topic.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishMessageCreated(final ChatMessageCreatedEvent event) {

        kafkaTemplate.send(KafkaTopics.CHAT_MESSAGE_CREATED_V1,
                           event.buildingId().toString(),
                           event);
    }
}