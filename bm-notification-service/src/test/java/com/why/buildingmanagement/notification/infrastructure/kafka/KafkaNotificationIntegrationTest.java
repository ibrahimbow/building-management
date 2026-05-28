package com.why.buildingmanagement.notification.infrastructure.kafka;

import com.why.buildingmanagement.notification.application.port.in.CreateNotificationCommand;
import com.why.buildingmanagement.notification.application.port.in.CreateNotificationUseCase;
import com.why.buildingmanagement.notification.application.port.out.LoadBuildingTenantUsersPort;
import com.why.buildingmanagement.notification.domain.model.NotificationType;
import com.why.buildingmanagement.notification.infrastructure.kafka.config.KafkaConsumerConfig;
import com.why.buildingmanagement.notification.infrastructure.kafka.event.AnnouncementCreatedEvent;
import com.why.buildingmanagement.notification.infrastructure.kafka.event.ChatMessageCreatedEvent;
import com.why.buildingmanagement.notification.infrastructure.kafka.event.ShareAndHelpCommentCreatedEvent;
import com.why.buildingmanagement.notification.infrastructure.kafka.event.ShareAndHelpPostCreatedEvent;
import com.why.buildingmanagement.notification.infrastructure.kafka.listener.AnnouncementEventListener;
import com.why.buildingmanagement.notification.infrastructure.kafka.listener.ChatMessageEventListener;
import com.why.buildingmanagement.notification.infrastructure.kafka.listener.ShareAndHelpEventListener;
import com.why.buildingmanagement.notification.infrastructure.kafka.topic.KafkaTopics;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@Testcontainers
@EnableKafka
@EnableConfigurationProperties(KafkaProperties.class)
@SpringBootTest(
                classes = {
                                KafkaConsumerConfig.class,
                                AnnouncementEventListener.class,
                                ShareAndHelpEventListener.class,
                                ChatMessageEventListener.class,
                                KafkaNotificationIntegrationTest.KafkaProducerTestConfig.class
                },
                webEnvironment = SpringBootTest.WebEnvironment.NONE,
                properties = {
                                "spring.main.web-application-type=none",
                                "spring.kafka.consumer.group-id=bm-notification-test",
                                "spring.kafka.consumer.auto-offset-reset=earliest",
                                "spring.kafka.listener.missing-topics-fatal=false"
                })
class KafkaNotificationIntegrationTest {

    @Container
    static final ConfluentKafkaContainer kafka =
                    new ConfluentKafkaContainer(
                                    DockerImageName.parse("confluentinc/cp-kafka:7.8.0"));

    @DynamicPropertySource
    static void kafkaProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockitoBean
    private CreateNotificationUseCase createNotificationUseCase;

    @MockitoBean
    private LoadBuildingTenantUsersPort loadBuildingTenantUsersPort;

    @BeforeEach
    void resetMocks() {
        reset(createNotificationUseCase, loadBuildingTenantUsersPort);
    }

    @Test
    void shouldConsumeAnnouncementCreatedEventAndCreateNotificationForEveryTenant() throws Exception {
        final UUID buildingId = UUID.randomUUID();

        when(loadBuildingTenantUsersPort.loadTenantUserIds(buildingId))
                        .thenReturn(List.of(10L, 20L));

        kafkaTemplate.send(KafkaTopics.ANNOUNCEMENT_CREATED_V1, new AnnouncementCreatedEvent(
                        UUID.randomUUID(),
                        buildingId,
                        "Water maintenance",
                        "MAINTENANCE",
                        "Manager One",
                        Instant.now())).get();

        final ArgumentCaptor<CreateNotificationCommand> captor =
                        ArgumentCaptor.forClass(CreateNotificationCommand.class);

        await().atMost(Duration.ofSeconds(10))
                        .untilAsserted(() ->
                                        verify(createNotificationUseCase, times(2))
                                                        .createNotification(captor.capture()));

        assertThat(captor.getAllValues())
                        .extracting(CreateNotificationCommand::userId)
                        .containsExactlyInAnyOrder(10L, 20L);

        assertThat(captor.getAllValues())
                        .allSatisfy(command ->
                                        assertThat(command.type()).isEqualTo(NotificationType.ANNOUNCEMENT));
    }

    @Test
    void shouldConsumeShareAndHelpPostCreatedEventAndNotNotifyCreator() throws Exception {
        final UUID buildingId = UUID.randomUUID();

        when(loadBuildingTenantUsersPort.loadTenantUserIds(buildingId))
                        .thenReturn(List.of(10L, 20L, 30L));

        kafkaTemplate.send(KafkaTopics.SHARE_AND_HELP_POST_CREATED_V1, new ShareAndHelpPostCreatedEvent(
                        UUID.randomUUID(),
                        buildingId,
                        10L,
                        "Free table",
                        "Ibrahim",
                        Instant.now())).get();

        final ArgumentCaptor<CreateNotificationCommand> captor =
                        ArgumentCaptor.forClass(CreateNotificationCommand.class);

        await().atMost(Duration.ofSeconds(10))
                        .untilAsserted(() ->
                                        verify(createNotificationUseCase, times(2))
                                                        .createNotification(captor.capture()));

        assertThat(captor.getAllValues())
                        .extracting(CreateNotificationCommand::userId)
                        .containsExactlyInAnyOrder(20L, 30L);
    }

    @Test
    void shouldConsumeShareAndHelpCommentCreatedEventAndNotifyPostOwner() throws Exception {
        final UUID buildingId = UUID.randomUUID();

        kafkaTemplate.send(KafkaTopics.SHARE_AND_HELP_COMMENT_CREATED_V1, new ShareAndHelpCommentCreatedEvent(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        buildingId,
                        10L,
                        20L,
                        "Free table",
                        "Sarah",
                        Instant.now())).get();

        final ArgumentCaptor<CreateNotificationCommand> captor =
                        ArgumentCaptor.forClass(CreateNotificationCommand.class);

        await().atMost(Duration.ofSeconds(10))
                        .untilAsserted(() ->
                                        verify(createNotificationUseCase)
                                                        .createNotification(captor.capture()));

        assertThat(captor.getValue().userId()).isEqualTo(10L);
        assertThat(captor.getValue().buildingId()).isEqualTo(buildingId);
        assertThat(captor.getValue().type()).isEqualTo(NotificationType.SHARE_AND_HELP);
    }

    @Test
    void shouldConsumeChatMessageCreatedEventAndNotNotifySender() throws Exception {
        final UUID buildingId = UUID.randomUUID();

        when(loadBuildingTenantUsersPort.loadTenantUserIds(buildingId))
                        .thenReturn(List.of(10L, 20L, 30L));

        kafkaTemplate.send(KafkaTopics.CHAT_MESSAGE_CREATED_V1, new ChatMessageCreatedEvent(
                        UUID.randomUUID(),
                        buildingId,
                        10L,
                        "Ibrahim",
                        "Hello everyone",
                        null,
                        Instant.now())).get();

        final ArgumentCaptor<CreateNotificationCommand> captor =
                        ArgumentCaptor.forClass(CreateNotificationCommand.class);

        await().atMost(Duration.ofSeconds(10))
                        .untilAsserted(() ->
                                        verify(createNotificationUseCase, times(2))
                                                        .createNotification(captor.capture()));

        assertThat(captor.getAllValues())
                        .extracting(CreateNotificationCommand::userId)
                        .containsExactlyInAnyOrder(20L, 30L);
    }

    @TestConfiguration
    static class KafkaProducerTestConfig {

        @Bean
        KafkaProperties kafkaProperties() {
            return new KafkaProperties();
        }

        @Bean
        KafkaTemplate<String, Object> kafkaTemplate() {
            final Map<String, Object> config = new HashMap<>();
            config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
            config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

            return new KafkaTemplate<>(
                            new DefaultKafkaProducerFactory<>(config));
        }
    }
}