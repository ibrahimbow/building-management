ALTER TABLE buildings
ADD CONSTRAINT uk_buildings_manager_id UNIQUE (manager_id);