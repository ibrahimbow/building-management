package com.why.buildingmanagement.notification.infrastructure.kafka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.why.buildingmanagement.notification.infrastructure.kafka.event.AnnouncementCreatedEvent;
import com.why.buildingmanagement.notification.infrastructure.kafka.event.ChatMessageCreatedEvent;
import com.why.buildingmanagement.notification.infrastructure.kafka.event.ShareAndHelpCommentCreatedEvent;
import com.why.buildingmanagement.notification.infrastructure.kafka.event.ShareAndHelpPostCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, AnnouncementCreatedEvent> announcementCreatedConsumerFactory(
                    final KafkaProperties kafkaProperties) {

        return consumerFactory(
                        kafkaProperties,
                        AnnouncementCreatedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AnnouncementCreatedEvent>
    announcementCreatedKafkaListenerContainerFactory(
                    final ConsumerFactory<String, AnnouncementCreatedEvent> announcementCreatedConsumerFactory) {

        final ConcurrentKafkaListenerContainerFactory<String, AnnouncementCreatedEvent> factory =
                        new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(announcementCreatedConsumerFactory);

        return factory;
    }

    @Bean
    public ConsumerFactory<String, ShareAndHelpPostCreatedEvent> shareAndHelpPostCreatedConsumerFactory(
                    final KafkaProperties kafkaProperties) {

        return consumerFactory(
                        kafkaProperties,
                        ShareAndHelpPostCreatedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ShareAndHelpPostCreatedEvent>
    shareAndHelpPostCreatedKafkaListenerContainerFactory(
                    final ConsumerFactory<String, ShareAndHelpPostCreatedEvent> shareAndHelpPostCreatedConsumerFactory) {

        final ConcurrentKafkaListenerContainerFactory<String, ShareAndHelpPostCreatedEvent> factory =
                        new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(shareAndHelpPostCreatedConsumerFactory);

        return factory;
    }

    @Bean
    public ConsumerFactory<String, ShareAndHelpCommentCreatedEvent> shareAndHelpCommentCreatedConsumerFactory(
                    final KafkaProperties kafkaProperties) {

        return consumerFactory(
                        kafkaProperties,
                        ShareAndHelpCommentCreatedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ShareAndHelpCommentCreatedEvent>
    shareAndHelpCommentCreatedKafkaListenerContainerFactory(
                    final ConsumerFactory<String, ShareAndHelpCommentCreatedEvent> shareAndHelpCommentCreatedConsumerFactory) {

        final ConcurrentKafkaListenerContainerFactory<String, ShareAndHelpCommentCreatedEvent> factory =
                        new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(shareAndHelpCommentCreatedConsumerFactory);

        return factory;
    }

    @Bean
    public ConsumerFactory<String, ChatMessageCreatedEvent>
    chatMessageCreatedConsumerFactory(
                    final KafkaProperties kafkaProperties,
                    final ObjectMapper objectMapper) {

        final JsonDeserializer<ChatMessageCreatedEvent> jsonDeserializer =
                        new JsonDeserializer<>(
                                        ChatMessageCreatedEvent.class,
                                        objectMapper,
                                        false);

        jsonDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                        kafkaProperties.buildConsumerProperties(),
                        new StringDeserializer(),
                        jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChatMessageCreatedEvent>
    chatMessageCreatedKafkaListenerContainerFactory(
                    final ConsumerFactory<String, ChatMessageCreatedEvent> consumerFactory) {

        final ConcurrentKafkaListenerContainerFactory<String, ChatMessageCreatedEvent>
                        factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        return factory;
    }

    private <T> ConsumerFactory<String, T> consumerFactory(
                    final KafkaProperties kafkaProperties,
                    final Class<T> eventType) {

        final Map<String, Object> config = new HashMap<>(kafkaProperties.buildConsumerProperties());

        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        config.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);

        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, eventType.getName());

        config.put(JsonDeserializer.TRUSTED_PACKAGES,
                        "com.why.buildingmanagement.notification.infrastructure.kafka.event");

        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaConsumerFactory<>(config);
    }
}