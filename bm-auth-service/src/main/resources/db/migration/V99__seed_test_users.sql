INSERT INTO building_users (
    id,
    username,
    email,
    password_hash,
    role,
    enabled,
    created_at,
    phone_number,
    display_name
)
VALUES
(
    1001,
    'manager1',
    'manager1@bm.test',
    '$2a$10$vXFAT8W/HQrs0OzRNNpEX.MJx3xhsi3LSZBw73upIaqc0MaJFsSSu',
    'MANAGER',
    true,
    NOW(),
    '+32470000001',
    'Manager One'
),
(
    1002,
    'manager2',
    'manager2@bm.test',
    '$2a$10$vXFAT8W/HQrs0OzRNNpEX.MJx3xhsi3LSZBw73upIaqc0MaJFsSSu',
    'MANAGER',
    true,
    NOW(),
    '+32470000002',
    'Manager Two'
),
(
    1003,
    'manager3',
    'manager3@bm.test',
    '$2a$10$vXFAT8W/HQrs0OzRNNpEX.MJx3xhsi3LSZBw73upIaqc0MaJFsSSu',
    'MANAGER',
    true,
    NOW(),
    '+32470000003',
    'Manager Three'
)
ON CONFLICT (email) DO NOTHING;



INSERT INTO building_users (
    id,
    username,
    email,
    password_hash,
    role,
    enabled,
    created_at,
    phone_number,
    display_name
)
SELECT
    2000 + n,
    'tenant' || n,
    'tenant' || n || '@bm.test',
    '$2a$10$vXFAT8W/HQrs0OzRNNpEX.MJx3xhsi3LSZBw73upIaqc0MaJFsSSu',
    'TENANT',
    true,
    NOW(),
    '+32471' || LPAD(n::text, 6, '0'),
    'Tenant ' || n
FROM generate_series(1, 30) AS n
ON CONFLICT (email) DO NOTHING;