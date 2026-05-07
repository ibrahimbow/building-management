ALTER TABLE buildings
ADD COLUMN manager_id BIGINT;

UPDATE buildings
SET manager_id = 1
WHERE manager_id IS NULL;

ALTER TABLE buildings
ALTER COLUMN manager_id SET NOT NULL;

ALTER TABLE buildings
DROP COLUMN IF EXISTS manager_name;

ALTER TABLE buildings
DROP COLUMN IF EXISTS manager_email;