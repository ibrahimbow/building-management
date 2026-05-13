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