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
    '/images/avatar-sarah.png',
    'Looking for a Dog Walker',
    'I am looking for someone who can help walk my dog on weekday afternoons.',
    '/images/post-dog.jpg',
    NOW() - INTERVAL '7 days',
    NOW() - INTERVAL '7 days',
    NULL
),
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111199',
    3002,
    'Michael Rodriguez',
    '/images/avatar-michael.png',
    'Selling a Coffee Table',
    'I am selling a modern wooden coffee table in very good condition. Pickup is possible this weekend.',
    '/images/post-table.jpg',
    NOW() - INTERVAL '6 days',
    NOW() - INTERVAL '6 days',
    NULL
),
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111199',
    3003,
    'Emma Williams',
    '/images/avatar-emma.png',
    'Weekly Yoga Group',
    'Would anyone like to join a small weekly yoga session in the community room?',
    '/images/post-yoga.jpg',
    NOW() - INTERVAL '5 days',
    NOW() - INTERVAL '5 days',
    NULL
),
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111199',
    3001,
    'Ahlam Haloomy',
    '/images/avatar-sarah.png',
    'Missing Package',
    'A small parcel may have been delivered to the wrong apartment. Please message me if you have seen it.',
    '/images/post-package.jpg',
    NOW() - INTERVAL '4 days',
    NOW() - INTERVAL '4 days',
    NULL
),
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111199',
    3002,
    'Michael Rodriguez',
    '/images/avatar-michael.png',
    'Need to Borrow a Drill',
    'Does anyone have a drill I could borrow for one evening to install a shelf?',
    '/images/post-tools.jpg',
    NOW() - INTERVAL '3 days',
    NOW() - INTERVAL '3 days',
    NULL
),
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111199',
    3003,
    'Emma Williams',
    '/images/avatar-emma.png',
    'Weekend Cycling Group',
    'I am planning a relaxed cycling trip this Saturday morning. Everyone is welcome to join.',
    '/images/post-cycling.jpg',
    NOW() - INTERVAL '2 days',
    NOW() - INTERVAL '2 days',
    NULL
),
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111199',
    3004,
    'Ibrahim Dev',
    '/images/avatar-david.png',
    'Ideas for Shared Spaces',
    'I would like to collect suggestions from residents about improving our shared building spaces.',
    '/images/post-feedback.jpg',
    NOW() - INTERVAL '1 day',
    NOW() - INTERVAL '1 day',
    NULL
);