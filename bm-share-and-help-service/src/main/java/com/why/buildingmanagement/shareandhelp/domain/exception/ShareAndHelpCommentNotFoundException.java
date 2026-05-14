package com.why.buildingmanagement.shareandhelp.domain.exception;

import java.util.UUID;

public class ShareAndHelpCommentNotFoundException extends RuntimeException {
    public ShareAndHelpCommentNotFoundException(final UUID commentId) {
        super("Share and Help comment was not found with id: " + commentId);
    }
}
