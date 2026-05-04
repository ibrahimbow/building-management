CREATE TABLE building_memberships (
    id UUID PRIMARY KEY,
    building_id UUID NOT NULL,
    tenant_user_id BIGINT NOT NULL,
    tenant_email VARCHAR(255) NOT NULL,
    joined_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_membership_building
        FOREIGN KEY (building_id)
        REFERENCES buildings(id)
        ON DELETE CASCADE
);

-- prevent same tenant joining same building twice
CREATE UNIQUE INDEX uk_building_membership_building_tenant
    ON building_memberships(building_id, tenant_user_id);

-- performance index
CREATE INDEX idx_membership_building_id
    ON building_memberships(building_id);