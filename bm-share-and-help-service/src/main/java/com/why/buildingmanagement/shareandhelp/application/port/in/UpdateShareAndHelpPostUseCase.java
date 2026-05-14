package com.why.buildingmanagement.shareandhelp.application.port.in;

import com.why.buildingmanagement.shareandhelp.application.result.ShareAndHelpPostResult;
import jakarta.validation.Valid;

public interface UpdateShareAndHelpPostUseCase {

    ShareAndHelpPostResult update(@Valid UpdateShareAndHelpPostCommand command);
}