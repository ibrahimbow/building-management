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
    'michael.r',
    'michael.r@bm.test',
    '$2a$10$vXFAT8W/HQrs0OzRNNpEX.MJx3xhsi3LSZBw73upIaqc0MaJFsSSu',
    'Michael Rodriguez',
    '+32471001002',
    '/api/files/profile_avatar/avatar_michael.png',
    'TENANT',
    true,
    NOW()
),
(
    3003,
    'emma.w',
    'emma.w@bm.test',
    '$2a$10$vXFAT8W/HQrs0OzRNNpEX.MJx3xhsi3LSZBw73upIaqc0MaJFsSSu',
    'Emma Williams',
    '+32471001003',
    '/api/files/profile_avatar/avatar_emma.png',
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
    '/api/files/profile_avatar/ibrahimAref.png',
    'TENANT',
    true,
    NOW()
),
(
    3005,
    'sophie sasa',
    'sophie.manager@bm.test',
    '$2a$10$vXFAT8W/HQrs0OzRNNpEX.MJx3xhsi3LSZBw73upIaqc0MaJFsSSu',
    'sophie Mad',
    '+32471001004',
    '/api/files/profile_avatar/avatar_emma.png',
    'MANAGER',
     true,
     NOW()
)
ON CONFLICT DO NOTHING;
