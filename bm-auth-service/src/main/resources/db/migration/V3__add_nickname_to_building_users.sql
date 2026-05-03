ALTER TABLE building_users
ADD COLUMN nickname VARCHAR(80);

UPDATE building_users
SET nickname = username
WHERE nickname IS NULL;

ALTER TABLE building_users
ALTER COLUMN nickname SET NOT NULL;