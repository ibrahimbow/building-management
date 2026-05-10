CREATE TABLE announcements
(
    id                    UUID PRIMARY KEY,
    building_id           UUID                     NOT NULL,
    created_by_manager_id BIGINT                   NOT NULL,
    created_by            VARCHAR(255)             NOT NULL,
    title                 VARCHAR(255)             NOT NULL,
    message               TEXT                     NOT NULL,
    category              VARCHAR(100)             NOT NULL,
    icon                  VARCHAR(100),
    image_url             VARCHAR(500),
    created_at            TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at            TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_announcements_building_id_created_at
    ON announcements(building_id, created_at DESC);