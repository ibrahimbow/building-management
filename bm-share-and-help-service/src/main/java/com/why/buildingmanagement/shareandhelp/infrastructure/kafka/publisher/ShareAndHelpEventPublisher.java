package com.why.buildingmanagement.shareandhelp.infrastructure.kafka.publisher;

import com.why.buildingmanagement.shareandhelp.infrastructure.kafka.event.ShareAndHelpCommentCreatedEvent;
import com.why.buildingmanagement.shareandhelp.infrastructure.kafka.event.ShareAndHelpPostCreatedEvent;
import com.why.buildingmanagement.shareandhelp.infrastructure.kafka.topic.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShareAndHelpEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPostCreated(final ShareAndHelpPostCreatedEvent event) {

        kafkaTemplate.send(KafkaTopics.SHARE_AND_HELP_POST_CREATED_V1,
                        event.buildingId().toString(),
                        event);
    }

    public void publishCommentCreated(final ShareAndHelpCommentCreatedEvent event) {

        kafkaTemplate.send(KafkaTopics.SHARE_AND_HELP_COMMENT_CREATED_V1,
                        event.buildingId().toString(),
                        event);
    }
}