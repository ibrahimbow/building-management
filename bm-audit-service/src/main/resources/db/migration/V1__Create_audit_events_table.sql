CREATE TABLE audit_events(
    id UUID             PRIMARY KEY,
    user_id             BIGINT,
    username            VARCHAR(100),
    event_type          VARCHAR(100) NOT NULL,
    description         TEXT NOT NULL,
    created_at          TIMESTAMP NOT NULL
);

CREATE INDEX idx_audit_events_user_id
    ON audit_events(user_id);

CREATE INDEX idx_audit_events_event_type
    ON audit_events(event_type);

CREATE INDEX idx_audit_events_created_at
    ON audit_events(created_at);