-- Demo dataset for AI clustering.
-- Target DB: PostgreSQL.
-- This script is idempotent and does not create brokers or administrators.
-- It assumes the default seed broker exists with id = 1.

BEGIN;

DO $$
BEGIN
    ALTER TABLE risk_factor_configuration
        DROP CONSTRAINT IF EXISTS risk_factor_configuration_level_check;

    ALTER TABLE risk_factor_configuration
        ADD CONSTRAINT risk_factor_configuration_level_check
        CHECK (level IN ('COUNTRY', 'COUNTY', 'CITY', 'BUILDING_TYPE', 'RISK_FACTOR_TYPE'));
END $$;

INSERT INTO country (id, name) VALUES
    (1, 'Romania'),
    (2, 'Germany')
ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name;

INSERT INTO county (id, name, country_id) VALUES
    (1, 'Bucuresti', 1),
    (2, 'Cluj', 1),
    (3, 'Iasi', 1),
    (4, 'Timis', 1),
    (5, 'Constanta', 1),
    (6, 'Brasov', 1),
    (7, 'Bavaria', 2),
    (8, 'Berlin', 2)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    country_id = EXCLUDED.country_id;

INSERT INTO city (id, name, county_id) VALUES
    (1, 'Bucuresti Sector 1', 1),
    (2, 'Bucuresti Sector 2', 1),
    (3, 'Cluj-Napoca', 2),
    (4, 'Turda', 2),
    (5, 'Iasi', 3),
    (6, 'Timisoara', 4),
    (7, 'Constanta', 5),
    (8, 'Mangalia', 5),
    (9, 'Brasov', 6),
    (10, 'Munich', 7),
    (11, 'Berlin', 8)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    county_id = EXCLUDED.county_id;

INSERT INTO risk_factor (id, type) VALUES
    (1, 'FLOOD_ZONE'),
    (2, 'EARTHQUAKE_RISK_ZONE'),
    (3, 'WINDSTORM_ZONE'),
    (4, 'LANDSLIDE_RISK')
ON CONFLICT (id) DO UPDATE SET type = EXCLUDED.type;

INSERT INTO currency (id, code, name, exchange_rate_to_base, is_active) VALUES
    (1, 'RON', 'Romanian Leu', 1.000000, true),
    (2, 'EUR', 'Euro', 4.975000, true),
    (3, 'USD', 'US Dollar', 4.550000, true)
ON CONFLICT (id) DO UPDATE SET
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    exchange_rate_to_base = EXCLUDED.exchange_rate_to_base,
    is_active = EXCLUDED.is_active;

INSERT INTO fee_configuration (id, name, type, percentage, effective_from, effective_to, is_active) VALUES
    (1, 'Standard broker fee', 'BROKER_COMMISSION', 5.000000, '2020-01-01', NULL, true),
    (2, 'Admin fee', 'ADMIN_FEE', 2.000000, '2020-01-01', NULL, true),
    (1001, 'Low risk discount', 'RISK_ADJUSTMENT', -1.000000, '2024-01-01', NULL, true),
    (1002, 'High value review fee', 'RISK_ADJUSTMENT', 3.000000, '2024-01-01', NULL, true)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    type = EXCLUDED.type,
    percentage = EXCLUDED.percentage,
    effective_from = EXCLUDED.effective_from,
    effective_to = EXCLUDED.effective_to,
    is_active = EXCLUDED.is_active;

INSERT INTO risk_factor_configuration (id, level, reference_id, adjustment_percentage, is_active) VALUES
    (1, 'CITY', '1', 1.500000, true),
    (2, 'CITY', '2', 1.000000, true),
    (1001, 'CITY', '3', 0.800000, true),
    (1002, 'CITY', '5', 2.400000, true),
    (1003, 'CITY', '7', 4.200000, true),
    (1004, 'CITY', '8', 3.700000, true),
    (1005, 'CITY', '9', 2.900000, true),
    (1006, 'CITY', '10', 0.600000, true),
    (1007, 'CITY', '11', 1.100000, true),
    (1010, 'COUNTY', '5', 2.000000, true),
    (1011, 'COUNTY', '6', 1.700000, true),
    (1012, 'COUNTRY', '2', 0.500000, true),
    (1020, 'BUILDING_TYPE', 'RESIDENTIAL', 0.500000, true),
    (1021, 'BUILDING_TYPE', 'OFFICE', 1.800000, true),
    (1022, 'BUILDING_TYPE', 'INDUSTRIAL', 4.500000, true),
    (1030, 'RISK_FACTOR_TYPE', 'FLOOD_ZONE', 3.000000, true),
    (1031, 'RISK_FACTOR_TYPE', 'EARTHQUAKE_RISK_ZONE', 4.000000, true),
    (1032, 'RISK_FACTOR_TYPE', 'WINDSTORM_ZONE', 2.000000, true),
    (1033, 'RISK_FACTOR_TYPE', 'LANDSLIDE_RISK', 3.500000, true)
ON CONFLICT (id) DO UPDATE SET
    level = EXCLUDED.level,
    reference_id = EXCLUDED.reference_id,
    adjustment_percentage = EXCLUDED.adjustment_percentage,
    is_active = EXCLUDED.is_active;

INSERT INTO client (id, type, country_code, name, identification_number, email, phone, address)
SELECT
    1000 + gs AS id,
    CASE WHEN gs % 4 = 0 THEN 'COMPANY' ELSE 'INDIVIDUAL' END AS type,
    CASE WHEN gs IN (9, 18, 27, 36) THEN 'DE' ELSE 'RO' END AS country_code,
    CASE
        WHEN gs % 4 = 0 THEN 'Company ' || gs
        ELSE 'Client ' || gs
    END AS name,
    CASE
        WHEN gs % 4 = 0 THEN 'CUIAI' || lpad(gs::text, 8, '0')
        ELSE 'CNPAI' || lpad(gs::text, 8, '0')
    END AS identification_number,
    'ai.client' || gs || '@example.com' AS email,
    '+4077000' || lpad(gs::text, 4, '0') AS phone,
    'Demo address ' || gs AS address
FROM generate_series(1, 36) AS gs
ON CONFLICT (id) DO UPDATE SET
    type = EXCLUDED.type,
    country_code = EXCLUDED.country_code,
    name = EXCLUDED.name,
    identification_number = EXCLUDED.identification_number,
    email = EXCLUDED.email,
    phone = EXCLUDED.phone,
    address = EXCLUDED.address;

INSERT INTO building (id, client_id, street, number, city_id, construction_year, type, number_of_floors, surface_area, insured_value)
SELECT
    2000 + gs AS id,
    1000 + ((gs - 1) % 36) + 1 AS client_id,
    CASE
        WHEN gs % 3 = 0 THEN 'Industrial Park'
        WHEN gs % 3 = 1 THEN 'Central Avenue'
        ELSE 'Residential Lane'
    END AS street,
    (10 + gs)::text AS number,
    CASE
        WHEN gs % 12 = 0 THEN 10
        WHEN gs % 11 = 0 THEN 11
        ELSE ((gs - 1) % 9) + 1
    END AS city_id,
    CASE
        WHEN gs % 5 = 0 THEN 1965 + (gs % 15)
        WHEN gs % 3 = 0 THEN 1985 + (gs % 20)
        ELSE 2005 + (gs % 18)
    END AS construction_year,
    CASE
        WHEN gs % 5 = 0 THEN 'INDUSTRIAL'
        WHEN gs % 3 = 0 THEN 'OFFICE'
        ELSE 'RESIDENTIAL'
    END AS type,
    CASE
        WHEN gs % 5 = 0 THEN 3 + (gs % 5)
        WHEN gs % 3 = 0 THEN 5 + (gs % 12)
        ELSE 1 + (gs % 4)
    END AS number_of_floors,
    CASE
        WHEN gs % 5 = 0 THEN 1500 + gs * 35
        WHEN gs % 3 = 0 THEN 450 + gs * 12
        ELSE 70 + gs * 3
    END::double precision AS surface_area,
    CASE
        WHEN gs % 5 = 0 THEN 900000 + gs * 22000
        WHEN gs % 3 = 0 THEN 350000 + gs * 9000
        ELSE 85000 + gs * 4500
    END::double precision AS insured_value
FROM generate_series(1, 72) AS gs
ON CONFLICT (id) DO UPDATE SET
    client_id = EXCLUDED.client_id,
    street = EXCLUDED.street,
    number = EXCLUDED.number,
    city_id = EXCLUDED.city_id,
    construction_year = EXCLUDED.construction_year,
    type = EXCLUDED.type,
    number_of_floors = EXCLUDED.number_of_floors,
    surface_area = EXCLUDED.surface_area,
    insured_value = EXCLUDED.insured_value;

INSERT INTO building_risk_factor (building_id, risk_factor_id)
SELECT 2000 + gs, 1
FROM generate_series(1, 72) AS gs
WHERE gs % 4 = 0 OR gs % 9 = 0
ON CONFLICT DO NOTHING;

INSERT INTO building_risk_factor (building_id, risk_factor_id)
SELECT 2000 + gs, 2
FROM generate_series(1, 72) AS gs
WHERE gs % 5 = 0 OR gs % 7 = 0
ON CONFLICT DO NOTHING;

INSERT INTO building_risk_factor (building_id, risk_factor_id)
SELECT 2000 + gs, 3
FROM generate_series(1, 72) AS gs
WHERE gs % 6 = 0 OR gs % 11 = 0
ON CONFLICT DO NOTHING;

INSERT INTO building_risk_factor (building_id, risk_factor_id)
SELECT 2000 + gs, 4
FROM generate_series(1, 72) AS gs
WHERE gs % 8 = 0 OR gs % 13 = 0
ON CONFLICT DO NOTHING;

INSERT INTO policies (
    id,
    policy_number,
    client_id,
    building_id,
    broker_id,
    status,
    start_date,
    end_date,
    base_premium_amount,
    currency_id,
    final_premium,
    created_at,
    last_updated_at,
    cancellation_date,
    cancellation_reason
)
SELECT
    3000 + gs AS id,
    'POL-AI-' || lpad(gs::text, 5, '0') AS policy_number,
    b.client_id,
    b.id AS building_id,
    1 AS broker_id,
    CASE
        WHEN gs % 10 = 0 THEN 'CANCELLED'
        WHEN gs % 7 = 0 THEN 'EXPIRED'
        WHEN gs % 6 = 0 THEN 'DRAFT'
        ELSE 'ACTIVE'
    END AS status,
    (DATE '2024-01-01' + ((gs % 420) || ' days')::interval)::date AS start_date,
    (DATE '2024-01-01' + ((gs % 420) || ' days')::interval + interval '1 year')::date AS end_date,
    round((b.insured_value * 0.012)::numeric, 2) AS base_premium_amount,
    CASE WHEN gs % 6 = 0 THEN 2 ELSE 1 END AS currency_id,
    round((b.insured_value * 0.012 * (
        1
        + CASE WHEN b.type = 'INDUSTRIAL' THEN 0.09 WHEN b.type = 'OFFICE' THEN 0.05 ELSE 0.025 END
        + CASE WHEN b.construction_year < 1980 THEN 0.07 ELSE 0 END
        + CASE WHEN b.city_id IN (7, 8, 9) THEN 0.06 ELSE 0.015 END
    ))::numeric, 2) AS final_premium,
    now() - ((gs % 90) || ' days')::interval AS created_at,
    now() - ((gs % 30) || ' days')::interval AS last_updated_at,
    CASE WHEN gs % 10 = 0 THEN (DATE '2025-01-01' + ((gs % 90) || ' days')::interval)::date ELSE NULL END AS cancellation_date,
    CASE WHEN gs % 10 = 0 THEN 'Demo cancellation for AI dataset' ELSE NULL END AS cancellation_reason
FROM generate_series(1, 108) AS gs
JOIN building b ON b.id = 2000 + ((gs - 1) % 72) + 1
ON CONFLICT (id) DO UPDATE SET
    policy_number = EXCLUDED.policy_number,
    client_id = EXCLUDED.client_id,
    building_id = EXCLUDED.building_id,
    broker_id = EXCLUDED.broker_id,
    status = EXCLUDED.status,
    start_date = EXCLUDED.start_date,
    end_date = EXCLUDED.end_date,
    base_premium_amount = EXCLUDED.base_premium_amount,
    currency_id = EXCLUDED.currency_id,
    final_premium = EXCLUDED.final_premium,
    created_at = EXCLUDED.created_at,
    last_updated_at = EXCLUDED.last_updated_at,
    cancellation_date = EXCLUDED.cancellation_date,
    cancellation_reason = EXCLUDED.cancellation_reason;

SELECT setval('country_id_seq', (SELECT COALESCE(MAX(id), 1) FROM country));
SELECT setval('county_id_seq', (SELECT COALESCE(MAX(id), 1) FROM county));
SELECT setval('city_id_seq', (SELECT COALESCE(MAX(id), 1) FROM city));
SELECT setval('risk_factor_id_seq', (SELECT COALESCE(MAX(id), 1) FROM risk_factor));
SELECT setval('currency_id_seq', (SELECT COALESCE(MAX(id), 1) FROM currency));
SELECT setval('fee_configuration_id_seq', (SELECT COALESCE(MAX(id), 1) FROM fee_configuration));
SELECT setval('risk_factor_configuration_id_seq', (SELECT COALESCE(MAX(id), 1) FROM risk_factor_configuration));
SELECT setval('client_id_seq', (SELECT COALESCE(MAX(id), 1) FROM client));
SELECT setval('building_id_seq', (SELECT COALESCE(MAX(id), 1) FROM building));
SELECT setval('policies_id_seq', (SELECT COALESCE(MAX(id), 1) FROM policies));

COMMIT;
