package com.why.buildingmanagement.file.infrastructure.storage;

import com.why.buildingmanagement.file.application.result.UploadedFileResult;
import com.why.buildingmanagement.file.domain.model.FileType;
import com.why.buildingmanagement.file.infrastructure.config.FileStorageProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

class LocalFileStorageAdapterTest {

    private LocalFileStorageAdapter adapter;

    private Path uploadRoot;

    @BeforeEach
    void setUp() throws Exception {

        uploadRoot = Files.createTempDirectory("file-storage-test");

        final FileStorageProperties properties =
                new FileStorageProperties(uploadRoot.toString());

        adapter = new LocalFileStorageAdapter(properties);
    }

    @AfterEach
    void tearDown() throws Exception {

        Files.walk(uploadRoot)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (final Exception ignored) {
                    }
                });
    }

    @Test
    void shouldStoreFile() throws Exception {

        final MockMultipartFile file = createFile();

        final UploadedFileResult result =
                adapter.store(file, FileType.PROFILE_AVATAR);

        assertThat(result.fileName()).isNotBlank();
        assertThat(result.fileName()).endsWith(".png");
        assertThat(result.url()).contains("/api/files/PROFILE_AVATAR/");
        assertThat(result.contentType()).isEqualTo("image/png");
        assertThat(result.size()).isEqualTo(file.getSize());

        final Path storedFile =
                uploadRoot
                        .resolve("profile_avatar")
                        .resolve(result.fileName());

        assertThat(storedFile).exists();
    }

    @Test
    void shouldLoadFilePath() throws Exception {

        final Path storedFile = createStoredFile(
                FileType.CHAT_MESSAGE_IMAGE,
                "chat-image.png");

        final Path result =
                adapter.load(
                        FileType.CHAT_MESSAGE_IMAGE,
                        "chat-image.png");

        assertThat(result).isEqualTo(storedFile);
        assertThat(result).exists();
    }

    @Test
    void shouldReturnMissingFilePath() {

        final Path result =
                adapter.load(
                        FileType.ANNOUNCEMENT_IMAGE,
                        "missing.png");

        assertThat(result.getFileName().toString())
                .isEqualTo("missing.png");

        assertThat(result).doesNotExist();
    }

    private static MockMultipartFile createFile() {

        return new MockMultipartFile(
                "file",
                "avatar.png",
                "image/png",
                "fake-image-content".getBytes());
    }

    private Path createStoredFile(final FileType type,
                                  final String fileName) throws Exception {

        final Path directory =
                uploadRoot.resolve(type.name().toLowerCase(java.util.Locale.ROOT));

        Files.createDirectories(directory);

        final Path file =
                directory.resolve(fileName);

        Files.writeString(file, "fake-image-content");

        return file;
    }
}