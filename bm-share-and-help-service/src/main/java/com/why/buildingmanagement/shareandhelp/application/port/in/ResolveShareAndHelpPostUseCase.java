package com.why.buildingmanagement.shareandhelp.application.port.in;

import com.why.buildingmanagement.shareandhelp.application.result.ShareAndHelpPostResult;

public interface ResolveShareAndHelpPostUseCase {

    ShareAndHelpPostResult resolvePost(ResolveShareAndHelpPostCommand command);

}
