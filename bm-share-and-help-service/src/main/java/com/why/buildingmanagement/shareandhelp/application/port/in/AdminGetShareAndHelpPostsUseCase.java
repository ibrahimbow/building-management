package com.why.buildingmanagement.shareandhelp.application.port.in;

import com.why.buildingmanagement.shareandhelp.application.result.ShareAndHelpPostResult;

import java.util.List;

public interface AdminGetShareAndHelpPostsUseCase {

    List<ShareAndHelpPostResult> getAllPostsForAdmin();
}