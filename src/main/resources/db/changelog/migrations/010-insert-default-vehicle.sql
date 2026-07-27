--liquibase formatted sql

--changeset rafael:010-insert-default-vehicle
--comment: Academic test data used by demonstrations and the automated payment SAGA BDD.
INSERT INTO vehicle (
    id,
    customer_id,
    license_plate,
    brand,
    model,
    model_year
)
VALUES (
    'bdd00000-0000-4000-8000-000000000001',
    '366941cf-9853-4514-ae99-1e1ea2b984ea',
    'BDD1A23',
    'PitFlow',
    'Veículo de Teste BDD',
    2026
)
ON CONFLICT DO NOTHING;

--rollback DELETE FROM vehicle
--rollback WHERE id = 'bdd00000-0000-4000-8000-000000000001';
