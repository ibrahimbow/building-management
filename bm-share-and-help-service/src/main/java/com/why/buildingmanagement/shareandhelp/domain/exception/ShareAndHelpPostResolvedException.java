package com.why.buildingmanagement.shareandhelp.domain.exception;

import java.util.UUID;

public class ShareAndHelpPostResolvedException extends RuntimeException {
    public ShareAndHelpPostResolvedException(final UUID postId) {
        super("Share and Help post is already resolved: " + postId);
    }
}
