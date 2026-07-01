-- =========================================
-- SHARE AND HELP POSTS
-- =========================================

INSERT INTO share_and_help_posts (
    id,
    building_id,
    created_by_user_id,
    created_by_display_name,
    created_by_avatar_url,
    title,
    description,
    image_url,
    created_at,
    updated_at,
    deleted_at
)
VALUES
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111199',
    3001,
    'Ahlam Haloomy',
    '/api/files/PROFILE_AVATAR/avatar_sarah.png',
    'Looking for a Dog Walker',
    'I am looking for someone who can help walk my dog on weekday afternoons.',
    '/api/files/SHARE_AND_HELP_IMAGE/post-dog.png',
    NOW() - INTERVAL '7 days',
    NOW() - INTERVAL '7 days',
    NULL
),
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111199',
    3002,
    'Saamoo',
    '/api/files/PROFILE_AVATAR/avatar_michael.png',
    'Extra Coffee Table',
    'I have unused modern wooden coffee table in very good condition. Pickup is possible this weekend.',
    '/api/files/SHARE_AND_HELP_IMAGE/post-table.png',
    NOW() - INTERVAL '6 days',
    NOW() - INTERVAL '6 days',
    NULL
),
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111199',
    3003,
    'Layan ',
    '/api/files/PROFILE_AVATAR/avatar_emma.png',
    'Weekly Yoga Group',
    'Would anyone like to join a small weekly yoga session in the park?',
    '/api/files/SHARE_AND_HELP_IMAGE/post-yoga.png',
    NOW() - INTERVAL '5 days',
    NOW() - INTERVAL '5 days',
    NULL
),
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111199',
    3001,
    'Ahlam Haloomy',
    '/api/files/PROFILE_AVATAR/avatar_sarah.png',
    'Missing Package',
    'A small parcel may have been delivered to the wrong apartment. Please message me if you have seen it.',
    '/api/files/SHARE_AND_HELP_IMAGE/post-package.png',
    NOW() - INTERVAL '4 days',
    NOW() - INTERVAL '4 days',
    NULL
),
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111199',
    3002,
    'Saamoo',
    '/api/files/PROFILE_AVATAR/avatar_michael.png',
    'Need to Borrow a Drill',
    'Does anyone have a drill I could borrow for one evening to install a shelf?',
    '/api/files/SHARE_AND_HELP_IMAGE/post-tools.png',
    NOW() - INTERVAL '3 days',
    NOW() - INTERVAL '3 days',
    NULL
),
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111199',
    3003,
    'Layan ',
    '/api/files/PROFILE_AVATAR/avatar_emma.png',
    'Weekend Cycling Group',
    'I am planning a relaxed cycling trip this Saturday morning. Everyone is welcome to join.',
    '/api/files/SHARE_AND_HELP_IMAGE/post-cycling.png',
    NOW() - INTERVAL '2 days',
    NOW() - INTERVAL '2 days',
    NULL
),
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111199',
    3004,
    'Ibrahim Dev',
    '/api/files/PROFILE_AVATAR/ibrahimAref.png',
    'Ideas for Shared Spaces',
    'I would like to collect suggestions from residents about improving our shared building spaces.',
    '/api/files/SHARE_AND_HELP_IMAGE/post-feedback.png',
    NOW() - INTERVAL '1 day',
    NOW() - INTERVAL '1 day',
    NULL
),
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111199',
    3004,
    'Ibrahim Dev',
    '/api/files/PROFILE_AVATAR/ibrahimAref.png',
    'Found Keys in the Lobby',
    'I found a set of keys near the building entrance. If you lost them, please contact me.',
    '/api/files/SHARE_AND_HELP_IMAGE/post-keys.png',
    NOW() - INTERVAL '12 hours',
    NOW() - INTERVAL '12 hours',
    NULL
),
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111199',
    3003,
    'Layan',
    '/api/files/PROFILE_AVATAR/avatar_emma.png',
    'Need Help Moving Furniture',
    'I will receive new furniture this weekend. If someone could help me bring it upstairs, I would really appreciate it.',
    '/api/files/SHARE_AND_HELP_IMAGE/post-furniture.png',
    NOW() - INTERVAL '5 hours',
    NOW() - INTERVAL '5 hours',
    NULL
);