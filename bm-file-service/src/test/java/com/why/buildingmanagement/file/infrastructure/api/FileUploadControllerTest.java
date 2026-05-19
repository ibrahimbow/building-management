package com.why.buildingmanagement.file.infrastructure.api;

import com.why.buildingmanagement.file.application.port.in.UploadFileCommand;
import com.why.buildingmanagement.file.application.port.in.UploadFileUseCase;
import com.why.buildingmanagement.file.application.result.UploadedFileResult;
import com.why.buildingmanagement.file.domain.model.FileType;
import com.why.buildingmanagement.file.infrastructure.storage.LocalFileStorageAdapter;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileUploadController.class)
@AutoConfigureMockMvc(addFilters = false)
class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UploadFileUseCase uploadFileUseCase;

    @MockitoBean
    private LocalFileStorageAdapter localFileStorageAdapter;

    @Test
    void shouldUploadFile() throws Exception {

        final MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                "image/png",
                "fake-image-content".getBytes());

        final UploadedFileResult result = new UploadedFileResult(
                "stored-avatar.png",
                "/api/files/PROFILE_AVATAR/stored-avatar.png",
                "image/png",
                file.getSize());

        when(uploadFileUseCase.upload(any(UploadFileCommand.class)))
                .thenReturn(result);

        mockMvc.perform(multipart("/api/files/upload")
                        .file(file)
                        .param("type", FileType.PROFILE_AVATAR.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName", is("stored-avatar.png")))
                .andExpect(jsonPath("$.url", is("/api/files/PROFILE_AVATAR/stored-avatar.png")))
                .andExpect(jsonPath("$.contentType", is("image/png")))
                .andExpect(jsonPath("$.size", is((int) file.getSize())));

        final ArgumentCaptor<UploadFileCommand> commandCaptor =
                ArgumentCaptor.forClass(UploadFileCommand.class);

        verify(uploadFileUseCase).upload(commandCaptor.capture());

        assertThat(commandCaptor.getValue().type()).isEqualTo(FileType.PROFILE_AVATAR);
        assertThat(commandCaptor.getValue().file().getOriginalFilename()).isEqualTo("avatar.png");
    }

    @Test
    void shouldReturnPngFile() throws Exception {

        final Path tempFile = Files.createTempFile("test-image", ".png");
        Files.writeString(tempFile, "fake-image-content");

        when(localFileStorageAdapter.load(
                FileType.PROFILE_AVATAR,
                "test-image.png"))
                .thenReturn(tempFile);

        mockMvc.perform(get("/api/files/{type}/{fileName}",
                        FileType.PROFILE_AVATAR,
                        "test-image.png"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"));
    }

    @Test
    void shouldReturnWebpFile() throws Exception {

        final Path tempFile = Files.createTempFile("test-image", ".webp");
        Files.writeString(tempFile, "fake-image-content");

        when(localFileStorageAdapter.load(
                FileType.CHAT_MESSAGE_IMAGE,
                "test-image.webp"))
                .thenReturn(tempFile);

        mockMvc.perform(get("/api/files/{type}/{fileName}",
                        FileType.CHAT_MESSAGE_IMAGE,
                        "test-image.webp"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/webp"));
    }

    @Test
    void shouldReturnJpegFileByDefault() throws Exception {

        final Path tempFile = Files.createTempFile("test-image", ".jpg");
        Files.writeString(tempFile, "fake-image-content");

        when(localFileStorageAdapter.load(
                FileType.ANNOUNCEMENT_IMAGE,
                "test-image.jpg"))
                .thenReturn(tempFile);

        mockMvc.perform(get("/api/files/{type}/{fileName}",
                        FileType.ANNOUNCEMENT_IMAGE,
                        "test-image.jpg"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"));
    }

    @Test
    void shouldReturnNotFoundWhenFileDoesNotExist() throws Exception {

        final Path missingFile = Path.of("missing-file.png");

        when(localFileStorageAdapter.load(
                FileType.SHARE_AND_HELP_IMAGE,
                "missing-file.png"))
                .thenReturn(missingFile);

        mockMvc.perform(get("/api/files/{type}/{fileName}",
                        FileType.SHARE_AND_HELP_IMAGE,
                        "missing-file.png"))
                .andExpect(status().isNotFound());
    }
}