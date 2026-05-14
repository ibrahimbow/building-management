package com.why.buildingmanagement.shareandhelp.application.port.in;

import com.why.buildingmanagement.shareandhelp.application.result.ShareAndHelpPostResult;
import jakarta.validation.Valid;

public interface AddCommentUseCase {

    ShareAndHelpPostResult addComment(@Valid AddCommentCommand command);
}