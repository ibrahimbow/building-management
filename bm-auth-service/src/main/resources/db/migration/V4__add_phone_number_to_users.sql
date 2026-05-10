ALTER TABLE building_users
    ADD COLUMN phone_number VARCHAR(30);

UPDATE building_users
SET phone_number = '+32000000000'
WHERE phone_number IS NULL;

ALTER TABLE building_users
    ALTER COLUMN phone_number SET NOT NULL;