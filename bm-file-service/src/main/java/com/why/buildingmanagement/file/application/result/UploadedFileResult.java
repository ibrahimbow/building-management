package com.why.buildingmanagement.file.application.result;

public record UploadedFileResult(
        String fileName,
        String url,
        String contentType,
        long size
) {
}