package com.why.buildingmanagement.file.application.port.in;

import com.why.buildingmanagement.file.application.result.UploadedFileResult;

public interface UploadFileUseCase {

    UploadedFileResult upload(final UploadFileCommand command);
}