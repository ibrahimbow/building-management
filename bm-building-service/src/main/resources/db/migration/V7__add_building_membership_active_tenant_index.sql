CREATE UNIQUE INDEX uk_building_membership_active_tenant
ON building_memberships (tenant_user_id)
WHERE left_at IS NULL;