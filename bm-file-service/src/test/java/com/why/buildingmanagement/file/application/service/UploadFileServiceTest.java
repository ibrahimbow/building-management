package com.why.buildingmanagement.file.application.service;

import com.why.buildingmanagement.file.application.port.in.UploadFileCommand;
import com.why.buildingmanagement.file.application.port.out.StoreFilePort;
import com.why.buildingmanagement.file.application.result.UploadedFileResult;
import com.why.buildingmanagement.file.domain.exception.InvalidFileException;
import com.why.buildingmanagement.file.domain.model.FileType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UploadFileServiceTest {

    @Mock
    private StoreFilePort storeFilePort;

    @InjectMocks
    private UploadFileService uploadFileService;

    @Test
    void shouldUploadValidPngFile() {

        final MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                "image/png",
                "fake-image-content".getBytes());

        final UploadedFileResult expectedResult = new UploadedFileResult(
                "stored-avatar.png",
                "/api/files/PROFILE_AVATAR/stored-avatar.png",
                "image/png",
                file.getSize());

        final UploadFileCommand command = new UploadFileCommand(
                file,
                FileType.PROFILE_AVATAR);

        when(storeFilePort.store(file, FileType.PROFILE_AVATAR))
                .thenReturn(expectedResult);

        final UploadedFileResult result = uploadFileService.upload(command);

        assertThat(result).isEqualTo(expectedResult);

        verify(storeFilePort).store(file, FileType.PROFILE_AVATAR);
    }

    @Test
    void shouldRejectEmptyFile() {

        final MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.png",
                "image/png",
                new byte[0]);

        final UploadFileCommand command = new UploadFileCommand(
                file,
                FileType.PROFILE_AVATAR);

        assertThatThrownBy(() -> uploadFileService.upload(command))
                .isInstanceOf(InvalidFileException.class)
                .hasMessage("File is empty.");

        verifyNoInteractions(storeFilePort);
    }

    @Test
    void shouldRejectUnsupportedContentType() {

        final MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                "fake-pdf-content".getBytes());

        final UploadFileCommand command = new UploadFileCommand(
                file,
                FileType.ANNOUNCEMENT_IMAGE);

        assertThatThrownBy(() -> uploadFileService.upload(command))
                .isInstanceOf(InvalidFileException.class)
                .hasMessage("Only JPG, PNG and WEBP images are allowed.");

        verifyNoInteractions(storeFilePort);
    }

    @Test
    void shouldRejectFileWithoutContentType() {

        final MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar",
                null,
                "fake-image-content".getBytes());

        final UploadFileCommand command = new UploadFileCommand(
                file,
                FileType.PROFILE_AVATAR);

        assertThatThrownBy(() -> uploadFileService.upload(command))
                .isInstanceOf(InvalidFileException.class)
                .hasMessage("Only JPG, PNG and WEBP images are allowed.");

        verifyNoInteractions(storeFilePort);
    }
}