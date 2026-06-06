package com.why.buildingmanagement.shareandhelp.application.port.in;

import java.util.UUID;

public interface AdminDeleteShareAndHelpPostUseCase {

    void deletePostByAdmin(UUID postId);

}