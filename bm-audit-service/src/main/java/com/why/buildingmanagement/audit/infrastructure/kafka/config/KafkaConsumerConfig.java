package com.why.buildingmanagement.audit.infrastructure.kafka.config;

import com.why.buildingmanagement.audit.infrastructure.kafka.event.AuditEventMessage;
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
    public ConsumerFactory<String, AuditEventMessage> auditEventConsumerFactory(final KafkaProperties kafkaProperties) {

        final Map<String, Object> config = new HashMap<>(kafkaProperties.buildConsumerProperties());

        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, AuditEventMessage.class.getName());
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.why.buildingmanagement.audit.infrastructure.kafka.event");
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AuditEventMessage>
    auditEventKafkaListenerContainerFactory(final ConsumerFactory<String, AuditEventMessage> auditEventConsumerFactory) {

        final ConcurrentKafkaListenerContainerFactory<String, AuditEventMessage> factory =
                        new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(auditEventConsumerFactory);

        return factory;
    }
}