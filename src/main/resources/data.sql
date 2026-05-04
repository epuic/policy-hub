INSERT INTO country (id, name)
VALUES (1, 'Romania')
    ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name;

INSERT INTO county (id, name, country_id) VALUES
                                              (1, 'Bucuresti', 1),
                                              (2, 'Cluj', 1),
                                              (3, 'Iasi', 1),
                                              (4, 'Timis', 1)
    ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name;

INSERT INTO city (id, name, county_id) VALUES
                                           (1, 'Bucuresti Sector 1', 1),
                                           (2, 'Bucuresti Sector 2', 1),
                                           (3, 'Cluj-Napoca', 2),
                                           (4, 'Turda', 2),
                                           (5, 'Iasi', 3),
                                           (6, 'Timisoara', 4)
    ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name;

INSERT INTO risk_factor (id, type) VALUES
    (1, 'FLOOD_ZONE'),
    (2, 'EARTHQUAKE_RISK_ZONE'),
    (3, 'WINDSTORM_ZONE'),
    (4, 'LANDSLIDE_RISK')
ON CONFLICT (id) DO UPDATE SET type = EXCLUDED.type;

INSERT INTO brokers (id, broker_code, name, email, phone, status, commission_percentage) VALUES
    (1, 'BRK001', 'Broker User', 'broker@example.com', '+40123456789', 'ACTIVE', 5.000000)
ON CONFLICT (id) DO NOTHING;

INSERT INTO administrators (id, name, email, role) VALUES
    (1, 'Administrator', 'admin@example.com', 'ADMIN')
ON CONFLICT (id) DO NOTHING;

INSERT INTO broker_auth (broker_id, email, password_hash) VALUES
    (1, 'broker@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy')
ON CONFLICT (broker_id) DO NOTHING;

INSERT INTO administrator_auth (administrator_id, email, password_hash) VALUES
    (1, 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy')
ON CONFLICT (administrator_id) DO NOTHING;

INSERT INTO currency (id, code, name, exchange_rate_to_base, is_active) VALUES
    (1, 'RON', 'Romanian Leu', 1.000000, true),
    (2, 'EUR', 'Euro', 4.975000, true)
ON CONFLICT (id) DO UPDATE SET code = EXCLUDED.code, name = EXCLUDED.name, exchange_rate_to_base = EXCLUDED.exchange_rate_to_base, is_active = EXCLUDED.is_active;

INSERT INTO fee_configuration (id, name, type, percentage, effective_from, effective_to, is_active) VALUES
    (1, 'Standard broker fee', 'BROKER_COMMISSION', 5.000000, '2020-01-01', NULL, true),
    (2, 'Admin fee', 'ADMIN_FEE', 2.000000, '2020-01-01', NULL, true)
ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, type = EXCLUDED.type, percentage = EXCLUDED.percentage, effective_from = EXCLUDED.effective_from, effective_to = EXCLUDED.effective_to, is_active = EXCLUDED.is_active;

INSERT INTO risk_factor_configuration (id, level, reference_id, adjustment_percentage, is_active) VALUES
    (1, 'CITY', '1', 1.500000, true),
    (2, 'CITY', '2', 1.000000, true)
ON CONFLICT (id) DO UPDATE SET level = EXCLUDED.level, reference_id = EXCLUDED.reference_id, adjustment_percentage = EXCLUDED.adjustment_percentage, is_active = EXCLUDED.is_active;

SELECT setval('country_id_seq', (SELECT MAX(id) FROM country));
SELECT setval('county_id_seq', (SELECT MAX(id) FROM county));
SELECT setval('city_id_seq', (SELECT MAX(id) FROM city));
SELECT setval('risk_factor_id_seq', (SELECT COALESCE(MAX(id), 1) FROM risk_factor));
SELECT setval('currency_id_seq', (SELECT COALESCE(MAX(id), 1) FROM currency));
SELECT setval('fee_configuration_id_seq', (SELECT COALESCE(MAX(id), 1) FROM fee_configuration));
SELECT setval('risk_factor_configuration_id_seq', (SELECT COALESCE(MAX(id), 1) FROM risk_factor_configuration));
SELECT setval('brokers_id_seq', (SELECT COALESCE(MAX(id), 1) FROM brokers));
SELECT setval('administrators_id_seq', (SELECT COALESCE(MAX(id), 1) FROM administrators));
SELECT setval('broker_auth_id_seq', (SELECT COALESCE(MAX(id), 1) FROM broker_auth));
SELECT setval('administrator_auth_id_seq', (SELECT COALESCE(MAX(id), 1) FROM administrator_auth));