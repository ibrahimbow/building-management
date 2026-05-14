package com.why.buildingmanagement.shareandhelp.domain.exception;

import java.util.UUID;

public class ShareAndHelpPostNotFoundException extends RuntimeException {
    public ShareAndHelpPostNotFoundException(final UUID postId) {
        super("Share and Help post was not found with id: " + postId);
    }
}
