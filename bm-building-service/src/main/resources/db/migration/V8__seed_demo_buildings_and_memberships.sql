INSERT INTO buildings (
    id,
    building_name,
    building_code,
    address,
    manager_id,
    total_apartments,
    emergency_phone
)
VALUES
(
    '11111111-1111-1111-1111-111111111111',
    'Antwerp Residence Alpha',
    'BM-TEST01',
    'Meir 10, 2000 Antwerp',
    1001,
    40,
    '+3231111111'
),
(
    '22222222-2222-2222-2222-222222222222',
    'Brussels Residence Beta',
    'BM-TEST02',
    'Rue Neuve 25, 1000 Brussels',
    1002,
    55,
    '+3222222222'
),
(
    '33333333-3333-3333-3333-333333333333',
    'Gent Residence Gamma',
    'BM-TEST03',
    'Veldstraat 15, 9000 Gent',
    1003,
    35,
    '+3293333333'
),
(
   '11111111-1111-1111-1111-111111111199',
    'Antwerp Residence Demo',
    'BM-TEST99',
    'Meir 10, 2000 Antwerp',
    3005,
    40,
    '+3231111111'
)
ON CONFLICT (building_code) DO NOTHING;

INSERT INTO building_memberships (
    id,
    building_id,
    tenant_user_id,
    tenant_username,
    tenant_email,
    tenant_phone_number,
    joined_at,
    left_at
)
SELECT
    gen_random_uuid(),
    CASE
        WHEN n BETWEEN 1 AND 10 THEN '11111111-1111-1111-1111-111111111111'::uuid
        WHEN n BETWEEN 11 AND 20 THEN '22222222-2222-2222-2222-222222222222'::uuid
        ELSE '33333333-3333-3333-3333-333333333333'::uuid
    END,
    2000 + n,
    'tenant' || n,
    'tenant' || n || '@bm.test',
    '+32471' || LPAD(n::text, 6, '0'),
    NOW(),
    NULL
FROM generate_series(1, 30) AS n
ON CONFLICT DO NOTHING;