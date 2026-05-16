CREATE TABLE chat_messages (
    id UUID PRIMARY KEY,
    building_id UUID NOT NULL,
    sender_user_id BIGINT NOT NULL,
    sender_display_name VARCHAR(150) NOT NULL,
    sender_avatar_url VARCHAR(500),
    content VARCHAR(2000),
    image_url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_chat_messages_building_id
    ON chat_messages(building_id);

CREATE INDEX idx_chat_messages_created_at
    ON chat_messages(created_at);



CREATE TABLE chat_reactions (
    id UUID PRIMARY KEY,
    message_id UUID NOT NULL,
    user_id BIGINT NOT NULL,
    emoji VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_chat_reactions_message_id
        FOREIGN KEY (message_id)
        REFERENCES chat_messages(id)
        ON DELETE CASCADE
);

CREATE UNIQUE INDEX uk_chat_reactions_message_user_emoji
    ON chat_reactions(message_id, user_id, emoji);

CREATE INDEX idx_chat_reactions_message_id
    ON chat_reactions(message_id);