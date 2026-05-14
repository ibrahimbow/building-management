package com.why.buildingmanagement.shareandhelp.application.port.in;

import jakarta.validation.Valid;

public interface DeleteShareAndHelpPostUseCase {

    void delete(@Valid DeleteShareAndHelpPostCommand command);
}