package com.why.buildingmanagement.chat.application.result;

public record ChatReactionSummaryResult(
        String emoji,
        long count,
        boolean reactedByCurrentUser) {
}