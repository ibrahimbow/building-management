package com.why.buildingmanagement.file.infrastructure.api;

import com.why.buildingmanagement.file.application.port.in.UploadFileCommand;
import com.why.buildingmanagement.file.application.port.in.UploadFileUseCase;
import com.why.buildingmanagement.file.application.result.UploadedFileResult;
import com.why.buildingmanagement.file.domain.model.FileType;
import com.why.buildingmanagement.file.infrastructure.storage.LocalFileStorageAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final UploadFileUseCase uploadFileUseCase;
    private final LocalFileStorageAdapter localFileStorageAdapter;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadedFileResult upload(@RequestParam("file") final MultipartFile file,
                                     @RequestParam("type") final FileType type) {
        return uploadFileUseCase.upload(new UploadFileCommand(file, type));
    }

    @GetMapping("/{type}/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable("type") final FileType type,
                                            @PathVariable("fileName") final String fileName) throws MalformedURLException {

        final Path filePath = localFileStorageAdapter.load(type, fileName);
        final Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}