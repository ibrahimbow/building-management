package com.why.buildingmanagement.notification.infrastructure.kafka.config;

import com.why.buildingmanagement.notification.infrastructure.kafka.event.AnnouncementCreatedEvent;
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
    public ConsumerFactory<String, AnnouncementCreatedEvent> consumerFactory(
                    final KafkaProperties kafkaProperties) {

        final Map<String, Object> config = new HashMap<>(kafkaProperties.buildConsumerProperties());

        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        config.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, AnnouncementCreatedEvent.class.getName());
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.why.buildingmanagement.notification.infrastructure.kafka.event");
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AnnouncementCreatedEvent> kafkaListenerContainerFactory(
                    final ConsumerFactory<String, AnnouncementCreatedEvent> consumerFactory) {

        final ConcurrentKafkaListenerContainerFactory<String, AnnouncementCreatedEvent> factory =
                        new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        return factory;
    }
}