package com.why.buildingmanagement.shareandhelp.application.port.in;

import java.util.UUID;

public interface AdminDeleteCommentUseCase {

    void deleteCommentByAdmin(UUID postId, UUID commentId);
}