package com.why.buildingmanagement.chat.application.port.in;

import com.why.buildingmanagement.chat.application.result.ChatMessageResult;

import java.util.List;

public interface GetBuildingChatUseCase {

    List<ChatMessageResult> getMessagesForCurrentTenantBuilding(final Long tenantUserId);

    List<ChatMessageResult> getMessagesForCurrentManagerBuilding(Long managerUserId);

}