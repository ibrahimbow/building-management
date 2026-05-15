package com.why.buildingmanagement.file.infrastructure.storage;

import com.why.buildingmanagement.file.application.port.out.StoreFilePort;
import com.why.buildingmanagement.file.application.result.UploadedFileResult;
import com.why.buildingmanagement.file.domain.model.FileType;
import com.why.buildingmanagement.file.infrastructure.config.FileStorageProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
public class LocalFileStorageAdapter implements StoreFilePort {

    private final Path uploadRoot;

    public LocalFileStorageAdapter(final FileStorageProperties properties) {
        this.uploadRoot = Path.of(properties.uploadDir()).toAbsolutePath().normalize();
    }

    @Override
    public UploadedFileResult store(final MultipartFile file, final FileType type) {
        try {
            final Path targetDirectory = uploadRoot.resolve(type.name().toLowerCase());
            Files.createDirectories(targetDirectory);

            final String originalFileName = file.getOriginalFilename();
            final String extension = extractExtension(originalFileName);
            final String storedFileName = UUID.randomUUID() + extension;

            final Path targetFile = targetDirectory.resolve(storedFileName).normalize();

            file.transferTo(targetFile);

            final String url = "/api/files/" + type.name().toLowerCase() + "/" + storedFileName;

            return new UploadedFileResult(
                    storedFileName,
                    url,
                    file.getContentType(),
                    file.getSize());
        } catch (final IOException exception) {
            throw new IllegalStateException("Could not store file.", exception);
        }
    }

    public Path load(final FileType type, final String fileName) {
        return uploadRoot
                .resolve(type.name().toLowerCase())
                .resolve(fileName)
                .normalize();
    }

    private String extractExtension(final String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }

        return fileName.substring(fileName.lastIndexOf("."));
    }

}