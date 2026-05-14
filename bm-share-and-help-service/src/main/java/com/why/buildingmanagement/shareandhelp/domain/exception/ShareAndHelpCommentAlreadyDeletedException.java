package com.why.buildingmanagement.shareandhelp.domain.exception;

public class ShareAndHelpCommentAlreadyDeletedException extends RuntimeException {

    public ShareAndHelpCommentAlreadyDeletedException() {
        super("Share and help comment is already deleted");
    }
}