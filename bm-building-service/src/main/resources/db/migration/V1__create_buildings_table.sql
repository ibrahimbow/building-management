CREATE TABLE buildings (
    id UUID PRIMARY KEY,
    building_name VARCHAR(255) NOT NULL,
    building_code VARCHAR(20) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    manager_name VARCHAR(255) NOT NULL,
    manager_email VARCHAR(255) NOT NULL,
    total_apartments INTEGER NOT NULL,
    emergency_phone VARCHAR(50) NOT NULL
);

CREATE INDEX idx_buildings_code ON buildings(building_code);