ALTER TABLE building_memberships
ADD COLUMN IF NOT EXISTS left_at TIMESTAMP NULL;

DROP INDEX IF EXISTS uk_building_membership_building_tenant;

CREATE UNIQUE INDEX IF NOT EXISTS uk_active_building_membership_tenant
ON building_memberships(tenant_user_id)
WHERE left_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_membership_tenant_active
ON building_memberships(tenant_user_id, left_at);