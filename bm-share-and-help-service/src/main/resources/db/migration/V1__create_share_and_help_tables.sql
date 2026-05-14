CREATE TABLE share_and_help_posts (
    id UUID PRIMARY KEY,
    building_id UUID NOT NULL,

    created_by_user_id BIGINT NOT NULL,
    created_by_display_name VARCHAR(150) NOT NULL,
    created_by_avatar_url VARCHAR(500),

    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    image_url VARCHAR(500),

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE share_and_help_comments (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL,

    comment TEXT NOT NULL,

    created_by_user_id BIGINT NOT NULL,
    created_by_display_name VARCHAR(150) NOT NULL,
    created_by_avatar_url VARCHAR(500),

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_share_help_comments_post
        FOREIGN KEY (post_id)
        REFERENCES share_and_help_posts(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_share_help_posts_building_id
    ON share_and_help_posts(building_id);

CREATE INDEX idx_share_help_posts_created_at
    ON share_and_help_posts(created_at DESC);

CREATE INDEX idx_share_help_posts_deleted_at
    ON share_and_help_posts(deleted_at);

CREATE INDEX idx_share_help_comments_post_id
    ON share_and_help_comments(post_id);

CREATE INDEX idx_share_help_comments_created_at
    ON share_and_help_comments(created_at ASC);

CREATE INDEX idx_share_help_comments_deleted_at
    ON share_and_help_comments(deleted_at);