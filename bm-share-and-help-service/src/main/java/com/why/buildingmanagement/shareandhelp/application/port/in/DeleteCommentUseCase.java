package com.why.buildingmanagement.shareandhelp.application.port.in;

import jakarta.validation.Valid;

public interface DeleteCommentUseCase {

    void deleteComment(@Valid DeleteCommentCommand command);
}