package com.why.buildingmanagement.chat.infrastructure.api.mapper;

import com.why.buildingmanagement.chat.application.result.ChatMessageResult;
import com.why.buildingmanagement.chat.application.result.ChatReactionResult;
import com.why.buildingmanagement.chat.application.result.ChatReactionSummaryResult;
import com.why.buildingmanagement.chat.infrastructure.api.dto.response.ChatMessageResponse;
import com.why.buildingmanagement.chat.infrastructure.api.dto.response.ChatReactionResponse;
import com.why.buildingmanagement.chat.infrastructure.api.dto.response.ChatReactionSummaryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatApiMapper {

    ChatMessageResponse toResponse(final ChatMessageResult result);

    ChatReactionResponse toResponse(final ChatReactionResult result);

    ChatReactionSummaryResponse toResponse(final ChatReactionSummaryResult result);
}