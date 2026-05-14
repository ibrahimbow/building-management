package com.why.buildingmanagement.shareandhelp.infrastructure.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddShareAndHelpCommentRequest(

        @NotBlank(message = "comment required")
        @Size(max = 2000, message = "comment must not exceed 2000 characters")
        String comment) {
}