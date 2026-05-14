package com.why.buildingmanagement.shareandhelp.application.port.in;

import com.why.buildingmanagement.shareandhelp.application.result.ShareAndHelpPostResult;

import java.util.List;
import java.util.UUID;

public interface GetShareAndHelpPostsUseCase {

    List<ShareAndHelpPostResult> getByBuildingId(UUID buildingId);
}