CREATE TABLE building_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(120) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

ALTER TABLE building_users
ADD CONSTRAINT uk_building_users_username UNIQUE (username);

ALTER TABLE building_users
ADD CONSTRAINT uk_building_users_email UNIQUE (email);

CREATE INDEX idx_building_users_username
ON building_users(username);

CREATE INDEX idx_building_users_email
ON building_users(email);