package com.why.buildingmanagement.file.application.port.out;

import com.why.buildingmanagement.file.application.result.UploadedFileResult;
import com.why.buildingmanagement.file.domain.model.FileType;
import org.springframework.web.multipart.MultipartFile;

public interface StoreFilePort {

    UploadedFileResult store(final MultipartFile file, final FileType type);
}