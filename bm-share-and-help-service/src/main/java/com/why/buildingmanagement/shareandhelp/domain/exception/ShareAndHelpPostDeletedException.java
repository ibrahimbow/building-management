package com.why.buildingmanagement.shareandhelp.domain.exception;

public class ShareAndHelpPostDeletedException extends RuntimeException {

    public ShareAndHelpPostDeletedException() {
        super("Share and help post is deleted");
    }
}