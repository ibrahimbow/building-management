package com.why.buildingmanagement.shareandhelp.application.port.out;

import com.why.buildingmanagement.shareandhelp.domain.model.ShareAndHelpPost;

public interface SaveShareAndHelpPostPort {

    ShareAndHelpPost save(final ShareAndHelpPost post);
}