package com.why.buildingmanagement.file.application.service;

import com.why.buildingmanagement.file.application.port.in.UploadFileCommand;
import com.why.buildingmanagement.file.application.port.in.UploadFileUseCase;
import com.why.buildingmanagement.file.application.port.out.StoreFilePort;
import com.why.buildingmanagement.file.application.result.UploadedFileResult;
import com.why.buildingmanagement.file.domain.exception.InvalidFileException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UploadFileService implements UploadFileUseCase {

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of("image/jpeg",
                                                                      "image/png",
                                                                      "image/webp");

    private final StoreFilePort storeFilePort;

    @Override
    public UploadedFileResult upload(final UploadFileCommand command) {

        validate(command);

        return storeFilePort.store(command.file(), command.type());
    }

    private void validate(final UploadFileCommand command) {

        if (command.file().isEmpty()) {
            throw new InvalidFileException("File is empty.");
        }

        final String contentType = command.file().getContentType();

        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {

            throw new InvalidFileException("Only JPG, PNG and WEBP images are allowed.");
        }
    }
}