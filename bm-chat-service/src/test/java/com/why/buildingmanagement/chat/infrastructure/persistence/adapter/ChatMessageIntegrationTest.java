package com.why.buildingmanagement.chat.infrastructure.persistence.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.why.buildingmanagement.chat.application.port.out.LoadTenantBuildingPort;
import com.why.buildingmanagement.chat.infrastructure.persistence.repository.ChatMessageRepository;
import com.why.buildingmanagement.chat.infrastructure.persistence.repository.ChatReactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class ChatMessageIntegrationTest {

    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final Long TENANT_USER_ID = 1001L;

    @Container
    static final org.testcontainers.postgresql.PostgreSQLContainer postgres =
                    new PostgreSQLContainer("postgres:16")
                                    .withDatabaseName("building_test_db")
                                    .withUsername("test")
                                    .withPassword("test");

    @DynamicPropertySource
    static void registerProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LoadTenantBuildingPort loadTenantBuildingPort;

    @Autowired
    private ChatReactionRepository chatReactionRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @BeforeEach
    void cleanDatabase() {
        chatReactionRepository.deleteAllInBatch();
        chatMessageRepository.deleteAllInBatch();
    }

    @Test
    @WithMockUser(username = "tenant", roles = "TENANT")
    void shouldCreateReactAndSoftDeleteChatMessage() throws Exception {

        when(loadTenantBuildingPort.loadActiveBuildingIdByTenantUserId(TENANT_USER_ID))
                        .thenReturn(BUILDING_ID);

        final String createMessageBody = objectMapper.writeValueAsString(Map.of(
                        "content", "Hello from chat integration test",
                        "imageUrl", ""
        ));

        final String createResponse = mockMvc.perform(post("/api/tenant/chat/messages")
                                        .with(csrf())
                                        .headers(tenantHeaders())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(createMessageBody))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id", notNullValue()))
                        .andExpect(jsonPath("$.senderUserId", is(TENANT_USER_ID.intValue())))
                        .andExpect(jsonPath("$.senderDisplayName", is("Integration Tenant")))
                        .andExpect(jsonPath("$.content", is("Hello from chat integration test")))
                        .andExpect(jsonPath("$.deleted", is(false)))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        final JsonNode createJson = objectMapper.readTree(createResponse);
        final String messageId = createJson.get("id").asText();

        mockMvc.perform(get("/api/tenant/chat/messages")
                                        .headers(tenantHeaders()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(1)));

        final String reactionBody = objectMapper.writeValueAsString(Map.of(
                        "emoji", "👍"
        ));

        mockMvc.perform(post("/api/tenant/chat/messages/{messageId}/reactions", messageId)
                                        .with(csrf())
                                        .headers(tenantHeaders())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(reactionBody))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.emoji", is("👍")))
                        .andExpect(jsonPath("$.userId", is(TENANT_USER_ID.intValue())));

        mockMvc.perform(delete("/api/tenant/chat/messages/{messageId}", messageId)
                                        .with(csrf())
                                        .headers(tenantHeaders()))
                        .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tenant/chat/messages")
                                        .headers(tenantHeaders()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].deleted", is(true)))
                        .andExpect(jsonPath("$[0].deletedAt", notNullValue()));
    }

    @Test
    @WithMockUser(username = "tenant", roles = "TENANT")
    void shouldAddAndRemoveReactionFromChatMessage() throws Exception {

        when(loadTenantBuildingPort.loadActiveBuildingIdByTenantUserId(TENANT_USER_ID))
                        .thenReturn(BUILDING_ID);

        final String createMessageBody = objectMapper.writeValueAsString(Map.of(
                        "content", "Message with removable reaction",
                        "imageUrl", ""));

        final String createResponse = mockMvc.perform(post("/api/tenant/chat/messages")
                                        .with(csrf())
                                        .headers(tenantHeaders())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(createMessageBody))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id", notNullValue()))
                        .andExpect(jsonPath("$.reactions", hasSize(0)))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        final JsonNode createJson = objectMapper.readTree(createResponse);
        final String messageId = createJson.get("id").asText();

        final String reactionBody = objectMapper.writeValueAsString(Map.of(
                        "emoji", "👍"));

        mockMvc.perform(post("/api/tenant/chat/messages/{messageId}/reactions", messageId)
                                        .with(csrf())
                                        .headers(tenantHeaders())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(reactionBody))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.emoji", is("👍")))
                        .andExpect(jsonPath("$.userId", is(TENANT_USER_ID.intValue())));

        mockMvc.perform(get("/api/tenant/chat/messages")
                                        .headers(tenantHeaders()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(1)))
                        .andExpect(jsonPath("$[0].id", is(messageId)))
                        .andExpect(jsonPath("$[0].reactions", hasSize(1)))
                        .andExpect(jsonPath("$[0].reactions[0].emoji", is("👍")))
                        .andExpect(jsonPath("$[0].reactions[0].count", is(1)))
                        .andExpect(jsonPath("$[0].reactions[0].reactedByCurrentUser", is(true)));

        mockMvc.perform(delete("/api/tenant/chat/messages/{messageId}/reactions", messageId)
                                        .with(csrf())
                                        .headers(tenantHeaders())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(reactionBody))
                        .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tenant/chat/messages")
                                        .headers(tenantHeaders()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(1)))
                        .andExpect(jsonPath("$[0].id", is(messageId)))
                        .andExpect(jsonPath("$[0].reactions", hasSize(0)));
    }

    private org.springframework.http.HttpHeaders tenantHeaders() {

        final org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();

        headers.add("X-User-Id", String.valueOf(TENANT_USER_ID));
        headers.add("X-User-Email", "tenant@test.com");
        headers.add("X-User-Role", "TENANT");
        headers.add("X-User-Display-Name", "Integration Tenant");
        headers.add("X-User-Avatar-Url", "");

        return headers;
    }
}