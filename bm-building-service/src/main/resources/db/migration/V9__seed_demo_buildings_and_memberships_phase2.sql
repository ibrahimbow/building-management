
    -- =========================================
    -- MEMBERSHIPS
    -- =========================================

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
    VALUES
    (
        gen_random_uuid(),
        '11111111-1111-1111-1111-111111111199',
        3001,
        'Ahlam Obad',
        'ahlamobad@bm.test',
        '+32471001001',
        NOW(),
        NULL
    ),
    (
        gen_random_uuid(),
        '11111111-1111-1111-1111-111111111199',
        3002,
        'sam.kim',
        'sam.kim@bm.test',
        '+32471001002',
        NOW(),
        NULL
    ),
    (
        gen_random_uuid(),
        '11111111-1111-1111-1111-111111111199',
        3003,
        'Layan Bra',
        'layan.bra@bm.test',
        '+32471001003',
        NOW(),
        NULL
    )
    ON CONFLICT DO NOTHING;
