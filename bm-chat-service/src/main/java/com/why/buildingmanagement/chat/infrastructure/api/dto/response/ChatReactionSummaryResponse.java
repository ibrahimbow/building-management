package com.why.buildingmanagement.chat.infrastructure.api.dto.response;

public record ChatReactionSummaryResponse(
        String emoji,
        long count,
        boolean reactedByCurrentUser) {
}