ALTER TABLE building_memberships
    ADD COLUMN tenant_username VARCHAR(255) NOT NULL DEFAULT 'unknown',
    ADD COLUMN tenant_phone_number VARCHAR(30) NOT NULL DEFAULT '+32000000000',
    ADD COLUMN left_at TIMESTAMP NULL;