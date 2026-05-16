package com.why.buildingmanagement.chat.application.port.out;

import java.util.UUID;

public interface DeleteChatReactionPort {

    void deleteByMessageIdAndUserIdAndEmoji(final UUID messageId,
                                            final Long userId,
                                            final String emoji);
}