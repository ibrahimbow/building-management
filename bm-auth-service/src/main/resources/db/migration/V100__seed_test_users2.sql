-- =========================================
-- V100__seed_test_users2.sql
-- Enterprise BM demo seed data
-- =========================================

-- =========================================
-- USERS
-- =========================================

INSERT INTO building_users (
    id,
    username,
    email,
    password_hash,
    display_name,
    phone_number,
    avatar_url,
    role,
    enabled,
    created_at
)
VALUES
(
    3001,
    'Ahlam Obad',
    'ahlamobad@bm.test',
    '$2a$10$vXFAT8W/HQrs0OzRNNpEX.MJx3xhsi3LSZBw73upIaqc0MaJFsSSu',
    'Ahlam Haloomy',
    '+32471001001',
    '/api/files/PROFILE_AVATAR/avatar_sarah.png',
    'TENANT',
    true,
    NOW()
),
(
    3002,
    'Sam Kim',
    'sam.kim@bm.test',
    '$2a$10$vXFAT8W/HQrs0OzRNNpEX.MJx3xhsi3LSZBw73upIaqc0MaJFsSSu',
    'Saamoo',
    '+32471001002',
    '/api/files/PROFILE_AVATAR/avatar_michael.png',
    'TENANT',
    true,
    NOW()
),
(
    3003,
    'Layan Flower',
    'layan.bra@bm.test',
    '$2a$10$vXFAT8W/HQrs0OzRNNpEX.MJx3xhsi3LSZBw73upIaqc0MaJFsSSu',
    'Layan',
    '+32471001003',
    '/api/files/PROFILE_AVATAR/avatar_emma.png',
    'TENANT',
    true,
    NOW()
),
(
    3004,
    'ibrahim Aref',
    'ibrahimAref@bm.test',
    '$2a$10$vXFAT8W/HQrs0OzRNNpEX.MJx3xhsi3LSZBw73upIaqc0MaJFsSSu',
    'Ibrahim Dev',
    '+32471001004',
    '/api/files/PROFILE_AVATAR/ibrahimAref.png',
    'TENANT',
    true,
    NOW()
),
(
    3005,
    'Sarah Ibrahim',
    'sarah.manager@bm.test',
    '$2a$10$vXFAT8W/HQrs0OzRNNpEX.MJx3xhsi3LSZBw73upIaqc0MaJFsSSu',
    'Sarah-Manger',
    '+32471001004',
    '/api/files/PROFILE_AVATAR/avatar_emma.png',
    'MANAGER',
     true,
     NOW()
)
ON CONFLICT DO NOTHING;
