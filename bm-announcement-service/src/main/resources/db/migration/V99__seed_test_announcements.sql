INSERT INTO announcements (
    id,
    building_id,
    created_by_manager_id,
    created_by,
    title,
    message,
    category,
    icon,
    image_url,
    created_at,
    updated_at
)
SELECT
    gen_random_uuid(),
    b.building_id,
    b.manager_id,
    b.manager_name,
    'Test Announcement ' || n || ' - ' || b.building_name,
    'This is test announcement number ' || n || ' for frontend testing.',
    CASE
        WHEN n % 3 = 0 THEN 'MAINTENANCE'
        WHEN n % 3 = 1 THEN 'GENERAL'
        ELSE 'EMERGENCY'
    END,
    CASE
        WHEN n % 3 = 0 THEN 'build'
        WHEN n % 3 = 1 THEN 'info'
        ELSE 'warning'
    END,
    NULL,
    NOW() - (n || ' days')::interval,
    NULL
FROM (
    VALUES
    ('11111111-1111-1111-1111-111111111111'::uuid, 1001, 'Manager One', 'Antwerp Residence Alpha'),
    ('22222222-2222-2222-2222-222222222222'::uuid, 1002, 'Manager Two', 'Brussels Residence Beta'),
    ('33333333-3333-3333-3333-333333333333'::uuid, 1003, 'Manager Three', 'Gent Residence Gamma')
) AS b(building_id, manager_id, manager_name, building_name)
CROSS JOIN generate_series(1, 9) AS n;



-- =========================================
-- ANNOUNCEMENTS
-- =========================================

INSERT INTO announcements (
    id,
    building_id,
    created_by_manager_id,
    created_by,
    title,
    message,
    category,
    icon,
    image_url,
    created_at,
    updated_at
)
VALUES
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111199',
    1004,
    'Sarah-Manger',
    'Lobby Renovation Schedule',
    'The lobby renovation will start next Monday at 08:00. Please expect light noise during daytime working hours.',
    'MAINTENANCE',
    'build',
    '/api/files/ANNOUNCEMENT_IMAGE/announcement-lobby.png',
    NOW() - INTERVAL '7 days',
    NULL
),
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111199',
    1004,
     'Sarah-Manger',
    'Water Supply Maintenance',
    'Water supply will be temporarily unavailable on Saturday between 09:00 and 12:00 for planned maintenance.',
    'MAINTENANCE',
    'water_drop',
    '/api/files/ANNOUNCEMENT_IMAGE/announcement-water.png',
    NOW() - INTERVAL '6 days',
    NULL
),
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111199',
    1004,
    'Sarah-Manger',
    'Community BBQ Event',
    'Residents are invited to a community BBQ in the shared garden this Sunday. Families are welcome.',
    'EVENT',
    'event',
    '/api/files/ANNOUNCEMENT_IMAGE/announcement-bbq.png',
    NOW() - INTERVAL '5 days',
    NULL
),
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111199',
    1004,
    'Sarah-Manger',
    'Parking Garage Inspection',
    'The annual parking garage safety inspection will take place this Friday afternoon.',
    'SAFETY',
    'local_parking',
    '/api/files/ANNOUNCEMENT_IMAGE/announcement-garage.png',
    NOW() - INTERVAL '4 days',
    NULL
),
(
    gen_random_uuid(),
    '11111111-1111-1111-1111-111111111199',
    1004,
    'Sarah-Manger',
    'Elevator Upgrade Completed',
    'The elevator upgrade has been completed successfully. Thank you for your patience during the works.',
    'GENERAL',
    'elevator',
    '/api/files/ANNOUNCEMENT_IMAGE/announcement-elevator.png',
    NOW() - INTERVAL '3 days',
    NULL
);