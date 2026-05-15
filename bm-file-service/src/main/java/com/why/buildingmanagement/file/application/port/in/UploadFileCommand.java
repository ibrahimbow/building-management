package com.why.buildingmanagement.file.application.port.in;

import com.why.buildingmanagement.file.domain.model.FileType;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UploadFileCommand(

        @NotNull(message = "file required")
        MultipartFile file,

        @NotNull(message = "file type required")
        FileType type) {
}