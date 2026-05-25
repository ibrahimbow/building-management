package com.why.buildingmanagement.shareandhelp.infrastructure.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.why.buildingmanagement.shareandhelp.application.port.out.LoadTenantBuildingPort;
import com.why.buildingmanagement.shareandhelp.infrastructure.kafka.publisher.ShareAndHelpEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class ShareAndHelpIntegrationTest {

    @Container
    static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16")
                    .withDatabaseName("building_test_db")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LoadTenantBuildingPort loadTenantBuildingPort;

    @MockitoBean
    private ShareAndHelpEventPublisher shareAndHelpEventPublisher;

    @Test
    void shouldCreateAndLoadShareAndHelpPost() throws Exception {

        final UUID buildingId = UUID.randomUUID();

        when(loadTenantBuildingPort.loadActiveBuildingIdByTenantUserId(1001L))
                .thenReturn(buildingId);

        final Map<String, Object> request = Map.of(
                "title", "Need a ladder",
                "description", "Does anyone have a ladder I can borrow this weekend?",
                "imageUrl", "");

        createPost(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Need a ladder"))
                .andExpect(jsonPath("$.description").value("Does anyone have a ladder I can borrow this weekend?"));

        mockMvc.perform(withTenantHeaders(get("/api/tenant/share-and-help/posts"))
                        .with(user("tenant").roles("TENANT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Need a ladder"));
    }

    @Test
    void shouldAddAndDeleteComment() throws Exception {

        final UUID buildingId = UUID.randomUUID();

        when(loadTenantBuildingPort.loadActiveBuildingIdByTenantUserId(1001L))
                .thenReturn(buildingId);

        final Map<String, Object> postRequest = Map.of(
                "title", "Need help",
                "description", "Can someone help me carry a box?",
                "imageUrl", "");

        final String response = createPost(postRequest)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final UUID postId = UUID.fromString(
                objectMapper.readTree(response).get("id").asText());

        final Map<String, Object> commentRequest = Map.of(
                "comment", "Yes, I can help you.");

        final String commentResponse = mockMvc.perform(
                        withTenantHeaders(post("/api/tenant/share-and-help/posts/{postId}/comments", postId))
                                .with(user("tenant").roles("TENANT"))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andExpect(jsonPath("$.comments[0].comment").value("Yes, I can help you."))
                .andReturn()
                .getResponse()
                .getContentAsString();

        final UUID commentId = UUID.fromString(
                objectMapper.readTree(commentResponse)
                        .get("comments")
                        .get(0)
                        .get("id")
                        .asText());

        mockMvc.perform(withTenantHeaders(
                        delete("/api/tenant/share-and-help/posts/{postId}/comments/{commentId}", postId, commentId))
                        .with(user("tenant").roles("TENANT"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldSoftDeletePostAndHideItFromBuildingFeed() throws Exception {

        final UUID buildingId = UUID.randomUUID();

        when(loadTenantBuildingPort.loadActiveBuildingIdByTenantUserId(1001L))
                .thenReturn(buildingId);

        final Map<String, Object> request = Map.of(
                "title", "Old post",
                "description", "This post will be deleted.",
                "imageUrl", "");

        final String response = createPost(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final UUID postId = UUID.fromString(
                objectMapper.readTree(response).get("id").asText());

        mockMvc.perform(withTenantHeaders(delete("/api/tenant/share-and-help/posts/{postId}", postId))
                        .with(user("tenant").roles("TENANT"))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        mockMvc.perform(withTenantHeaders(get("/api/tenant/share-and-help/posts"))
                        .with(user("tenant").roles("TENANT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private ResultActions createPost(final Map<String, Object> request) throws Exception {

        return mockMvc.perform(withTenantHeaders(post("/api/tenant/share-and-help/posts"))
                .with(user("tenant").roles("TENANT"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    private static MockHttpServletRequestBuilder withTenantHeaders(
            final MockHttpServletRequestBuilder request) {

        return request
                .header("X-User-Id", "1001")
                .header("X-User-Email", "tenant@test.com")
                .header("X-User-Role", "TENANT")
                .header("X-Username", "Tenant One")
                .header("X-User-Display-Name","Tenant DisplayName")
                .header("X-User-Avatar-Url", "");
    }
}