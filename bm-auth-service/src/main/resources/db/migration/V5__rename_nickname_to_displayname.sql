ALTER TABLE building_users
ADD COLUMN display_name VARCHAR(80);

UPDATE building_users
SET display_name = username
WHERE display_name IS NULL;

ALTER TABLE building_users
ALTER COLUMN display_name SET NOT NULL;