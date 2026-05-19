package com.why.buildingmanagement.file.infrastructure.api;

import com.why.buildingmanagement.file.domain.model.FileType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class FileUploadIntegrationTest {

    private static final Path UPLOAD_ROOT;

    static {
        try {
            UPLOAD_ROOT = Files.createTempDirectory("file-upload-integration-test");
        } catch (final Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    @DynamicPropertySource
    static void registerProperties(final DynamicPropertyRegistry registry) {
        registry.add("file.storage.upload-dir", UPLOAD_ROOT::toString);
    }

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void cleanUp() throws Exception {

        if (Files.exists(UPLOAD_ROOT)) {
            Files.walk(UPLOAD_ROOT)
                    .filter(path -> !path.equals(UPLOAD_ROOT))
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (final Exception ignored) {
                        }
                    });
        }
    }

    @Test
    void shouldUploadAndRetrieveFile() throws Exception {

        final MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                "image/png",
                "fake-image-content".getBytes());

        final String responseBody = mockMvc.perform(multipart("/api/files/upload")
                        .file(file)
                        .param("type", FileType.PROFILE_AVATAR.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName", notNullValue()))
                .andExpect(jsonPath("$.fileName", containsString(".png")))
                .andExpect(jsonPath("$.url", containsString("/api/files/PROFILE_AVATAR/")))
                .andExpect(jsonPath("$.contentType", is("image/png")))
                .andExpect(jsonPath("$.size", is((int) file.getSize())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        final String storedFileName =
                responseBody.replaceAll(".*\"fileName\":\"([^\"]+)\".*", "$1");

        final Path storedFile =
                UPLOAD_ROOT
                        .resolve("profile_avatar")
                        .resolve(storedFileName);

        assertThat(storedFile).exists();

        mockMvc.perform(get("/api/files/{type}/{fileName}",
                        FileType.PROFILE_AVATAR.name(),
                        storedFileName))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"));
    }
}